package net.mcreator.mut.affix.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.*;

public class MaterialBonusRegistry {
    private static MaterialBonusRegistry INSTANCE;
    private final Map<String, List<MaterialContext.AffixBonusEntry>> directedBonusCache = new HashMap<>();
    private final Map<String, Integer> enchantBonusCache = new HashMap<>();
    private final Map<String, Integer> minGuaranteedLevelCache = new HashMap<>();
    private final Map<String, Integer> maxLevelCapCache = new HashMap<>();
    private final List<TagDrivenEntry> tagDrivenEntries = new ArrayList<>();
    private final Map<String, MaterialBonusConfig.UniversalMaterial> tagUniversalMaterialCache = new HashMap<>();

    private MaterialBonusRegistry() { rebuild(); }

    public static MaterialBonusRegistry getInstance() {
        if (INSTANCE == null) INSTANCE = new MaterialBonusRegistry();
        return INSTANCE;
    }

    public void rebuild() {
        directedBonusCache.clear(); enchantBonusCache.clear();
        minGuaranteedLevelCache.clear(); maxLevelCapCache.clear();
        tagDrivenEntries.clear(); tagUniversalMaterialCache.clear();

        MaterialBonusConfig config = AffixDataLoader.getMaterialBonusConfig();
        if (config == null) return;

        if (config.getUniversalMaterials() != null) {
            for (MaterialBonusConfig.UniversalMaterial um : config.getUniversalMaterials()) {
                if (um.getItem() != null) {
                    if (um.getEnchantBonus() > 0) enchantBonusCache.put(um.getItem(), um.getEnchantBonus());
                    if (um.getMinGuaranteedLevel() > 0) minGuaranteedLevelCache.put(um.getItem(), um.getMinGuaranteedLevel());
                    if (um.getMaxLevelCap() > 0) maxLevelCapCache.put(um.getItem(), um.getMaxLevelCap());
                }
                if (um.getTag() != null) tagUniversalMaterialCache.put(um.getTag(), um);
            }
        }

        if (config.getDirectedMaterials() != null) {
            for (MaterialBonusConfig.DirectedMaterial dm : config.getDirectedMaterials()) {
                String key = dm.getItem() != null ? dm.getItem() : (dm.getTag() != null ? "#" + dm.getTag() : null);
                if (key == null) continue;
                List<MaterialContext.AffixBonusEntry> entries = new ArrayList<>();
                for (MaterialBonusConfig.AffixBonus ab : dm.getAffixBonuses()) {
                    entries.add(new MaterialContext.AffixBonusEntry(
                            ab.getTargetAffix(), ab.getFixedProbability(), ab.getMinLevel(), ab.getMaxLevel()));
                }
                directedBonusCache.put(key, entries);
            }
        }

        if (config.getTagDrivenMaterials() != null) {
            for (MaterialBonusConfig.TagDrivenMaterial tdm : config.getTagDrivenMaterials()) {
                if (tdm.getTag() == null) continue;
                List<MaterialContext.AffixBonusEntry> entries = new ArrayList<>();
                for (MaterialBonusConfig.AffixBonus ab : tdm.getAffixBonuses()) {
                    entries.add(new MaterialContext.AffixBonusEntry(
                            ab.getTargetAffix(), ab.getFixedProbability(), ab.getMinLevel(), ab.getMaxLevel()));
                }
                tagDrivenEntries.add(new TagDrivenEntry(tdm.getTag(), entries));
            }
        }
    }

    public MaterialContext evaluate(ItemStack additionStack) {
        if (additionStack.isEmpty()) return MaterialContext.empty();
        String itemId = BuiltInRegistries.ITEM.getKey(additionStack.getItem()).toString();

        int enchantBonus = enchantBonusCache.getOrDefault(itemId, 0);
        int minGuaranteedLevel = minGuaranteedLevelCache.getOrDefault(itemId, 0);
        int maxLevelCap = maxLevelCapCache.getOrDefault(itemId, 0);

        if (enchantBonus == 0) {
            for (Map.Entry<String, MaterialBonusConfig.UniversalMaterial> e : tagUniversalMaterialCache.entrySet()) {
                TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(e.getKey()));
                if (additionStack.is(tagKey)) {
                    enchantBonus = e.getValue().getEnchantBonus();
                    if (e.getValue().getMinGuaranteedLevel() > 0) minGuaranteedLevel = e.getValue().getMinGuaranteedLevel();
                    if (e.getValue().getMaxLevelCap() > 0) maxLevelCap = e.getValue().getMaxLevelCap();
                    break;
                }
            }
        }

        List<MaterialContext.AffixBonusEntry> affixBonuses = new ArrayList<>();
        List<MaterialContext.AffixBonusEntry> exact = directedBonusCache.get(itemId);
        if (exact != null) affixBonuses.addAll(exact);
        for (var entry : directedBonusCache.entrySet()) {
            if (entry.getKey().startsWith("#")) {
                TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(entry.getKey().substring(1)));
                if (additionStack.is(tagKey)) affixBonuses.addAll(entry.getValue());
            }
        }
        for (TagDrivenEntry tde : tagDrivenEntries) {
            TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(tde.tag));
            if (additionStack.is(tagKey)) affixBonuses.addAll(tde.bonuses);
        }

        return new MaterialContext(additionStack, enchantBonus, affixBonuses, minGuaranteedLevel, maxLevelCap);
    }

    private static class TagDrivenEntry {
        final String tag;
        final List<MaterialContext.AffixBonusEntry> bonuses;
        TagDrivenEntry(String tag, List<MaterialContext.AffixBonusEntry> bonuses) { this.tag = tag; this.bonuses = bonuses; }
    }
}
