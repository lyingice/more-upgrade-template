package net.mcreator.mut.affix.data;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 材料权重表运行时注册器
 * 根据 MaterialBonusConfig 和物品标签，确定材料提供的加成
 */
public class MaterialBonusRegistry {

    private static MaterialBonusRegistry INSTANCE;

    // 预处理缓存：item ID -> List<AffixBonusEntry>
    private final Map<String, List<MaterialContext.AffixBonusEntry>> directedBonusCache = new HashMap<>();

    // 预处理缓存：item ID -> universal level bonus
    private final Map<String, Double> universalBonusCache = new HashMap<>();

    // 预处理缓存：item ID -> material tier
    private final Map<String, Integer> materialTierCache = new HashMap<>();

    // 预处理缓存：item ID -> tier level bonus
    private final Map<String, Double> tierLevelBonusCache = new HashMap<>();

    // 预处理缓存：item ID -> min guaranteed level (材料保底)
    private final Map<String, Integer> minGuaranteedLevelCache = new HashMap<>();

    // 预处理缓存：item ID -> max level cap (材料等级上限)
    private final Map<String, Integer> maxLevelCapCache = new HashMap<>();

    // 预处理缓存：item ID -> tier max level cap (层级等级上限)
    private final Map<String, Integer> tierMaxLevelCapCache = new HashMap<>();

    // 标签驱动缓存
    private final List<TagDrivenEntry> tagDrivenEntries = new ArrayList<>();

    // 通用材料配置缓存（用于获取保底/上限）
    private final List<MaterialBonusConfig.UniversalMaterial> universalMaterialConfigs = new ArrayList<>();

    // 标签驱动通用材料索引：tag -> UniversalMaterial
    private final Map<String, MaterialBonusConfig.UniversalMaterial> tagUniversalMaterialCache = new HashMap<>();

    private MaterialBonusRegistry() {
        rebuild();
    }

    /**
     * 获取单例
     */
    public static MaterialBonusRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MaterialBonusRegistry();
        }
        return INSTANCE;
    }

    /**
     * 重建所有缓存（当配置重载时调用）
     */
    public void rebuild() {
        directedBonusCache.clear();
        universalBonusCache.clear();
        materialTierCache.clear();
        tierLevelBonusCache.clear();
        minGuaranteedLevelCache.clear();
        maxLevelCapCache.clear();
        tierMaxLevelCapCache.clear();
        tagDrivenEntries.clear();
        universalMaterialConfigs.clear();
        tagUniversalMaterialCache.clear();

        MaterialBonusConfig config = AffixDataLoader.getMaterialBonusConfig();
        if (config == null) return;

        // 1. 处理通用材料
        if (config.getUniversalMaterials() != null) {
            for (MaterialBonusConfig.UniversalMaterial um : config.getUniversalMaterials()) {
                universalMaterialConfigs.add(um);
                // 缓存 level_weight_bonus
                if (um.getItem() != null) {
                    universalBonusCache.put(um.getItem(), um.getLevelWeightBonus());
                    // 缓存保底和上限
                    if (um.getMinGuaranteedLevel() > 0) {
                        minGuaranteedLevelCache.put(um.getItem(), um.getMinGuaranteedLevel());
                    }
                    if (um.getMaxLevelCap() > 0) {
                        maxLevelCapCache.put(um.getItem(), um.getMaxLevelCap());
                    }
                }
                if (um.getTag() != null) {
                    universalBonusCache.put("#" + um.getTag(), um.getLevelWeightBonus());
                    tagUniversalMaterialCache.put(um.getTag(), um);
                }
            }
        }

        // 2. 处理定向材料
        if (config.getDirectedMaterials() != null) {
            for (MaterialBonusConfig.DirectedMaterial dm : config.getDirectedMaterials()) {
                String key;
                if (dm.getItem() != null) {
                    key = dm.getItem();
                } else if (dm.getTag() != null) {
                    key = "#" + dm.getTag();
                } else {
                    continue;
                }

                List<MaterialContext.AffixBonusEntry> entries = new ArrayList<>();
                for (MaterialBonusConfig.AffixBonus ab : dm.getAffixBonuses()) {
                    entries.add(new MaterialContext.AffixBonusEntry(
                            ab.getTargetAffix(),
                            ab.getAffixWeightMultiplier(),
                            ab.getLevelWeightBonus(),
                            ab.getMinLevel(),
                            ab.getMaxLevel()
                    ));
                }
                directedBonusCache.put(key, entries);
            }
        }

        // 3. 处理标签驱动材料
        if (config.getTagDrivenMaterials() != null) {
            for (MaterialBonusConfig.TagDrivenMaterial tdm : config.getTagDrivenMaterials()) {
                if (tdm.getTag() == null) continue;

                List<MaterialContext.AffixBonusEntry> entries = new ArrayList<>();
                for (MaterialBonusConfig.AffixBonus ab : tdm.getAffixBonuses()) {
                    entries.add(new MaterialContext.AffixBonusEntry(
                            ab.getTargetAffix(),
                            ab.getAffixWeightMultiplier(),
                            ab.getLevelWeightBonus(),
                            ab.getMinLevel(),
                            ab.getMaxLevel()
                    ));
                }
                tagDrivenEntries.add(new TagDrivenEntry(tdm.getTag(), entries));
            }
        }

        // 4. 处理材料品质层级
        if (config.getMaterialTiers() != null) {
            for (MaterialBonusConfig.MaterialTier mt : config.getMaterialTiers()) {
                if (mt.getItems() == null) continue;
                for (String itemId : mt.getItems()) {
                    materialTierCache.put(itemId, mt.getTier());
                    tierLevelBonusCache.put(itemId, mt.getLevelBonus());
                    if (mt.getMaxLevelCap() > 0) {
                        tierMaxLevelCapCache.put(itemId, mt.getMaxLevelCap());
                    }
                }
            }
        }
    }

    /**
     * 根据给定的物品，构建完整的 MaterialContext
     */
    public MaterialContext evaluate(ItemStack additionStack) {
        if (additionStack.isEmpty()) {
            return MaterialContext.empty();
        }

        String itemId = BuiltInRegistries.ITEM.getKey(additionStack.getItem()).toString();

        // 1. 通用等级加成 + 保底/上限
        double universalBonus = universalBonusCache.getOrDefault(itemId, 0.0);
        int minGuaranteedLevel = minGuaranteedLevelCache.getOrDefault(itemId, 0);
        int maxLevelCap = maxLevelCapCache.getOrDefault(itemId, 0);

        if (universalBonus == 0.0) {
            // 检查标签匹配
            for (Map.Entry<String, MaterialBonusConfig.UniversalMaterial> entry : tagUniversalMaterialCache.entrySet()) {
                String tagName = entry.getKey();
                TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(tagName));
                if (additionStack.is(tagKey)) {
                    universalBonus = entry.getValue().getLevelWeightBonus();
                    if (entry.getValue().getMinGuaranteedLevel() > 0) {
                        minGuaranteedLevel = entry.getValue().getMinGuaranteedLevel();
                    }
                    if (entry.getValue().getMaxLevelCap() > 0) {
                        maxLevelCap = entry.getValue().getMaxLevelCap();
                    }
                    break;
                }
            }
        }

        // 2. 定向词缀加成
        List<MaterialContext.AffixBonusEntry> affixBonuses = new ArrayList<>();

        // 先精确匹配 item ID
        List<MaterialContext.AffixBonusEntry> exactMatches = directedBonusCache.get(itemId);
        if (exactMatches != null) {
            affixBonuses.addAll(exactMatches);
        }

        // 再匹配 tag
        for (var entry : directedBonusCache.entrySet()) {
            if (entry.getKey().startsWith("#")) {
                String tagName = entry.getKey().substring(1);
                TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(tagName));
                if (additionStack.is(tagKey)) {
                    affixBonuses.addAll(entry.getValue());
                }
            }
        }

        // 匹配标签驱动条目
        for (TagDrivenEntry tde : tagDrivenEntries) {
            TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(tde.tag));
            if (additionStack.is(tagKey)) {
                affixBonuses.addAll(tde.bonuses);
            }
        }

        // 3. 材料品质层级
        int tier = materialTierCache.getOrDefault(itemId, 0);
        double tierLevelBonus = tierLevelBonusCache.getOrDefault(itemId, 0.0);
        int tierMaxCap = tierMaxLevelCapCache.getOrDefault(itemId, 0);

        // 4. 合并材料上限：取全局上限与层级上限的最大（更宽松的一个），
        //    但 minGuaranteedLevel 取最高值（更严格）
        int finalMaxLevelCap = Math.max(maxLevelCap, tierMaxCap);
        // 如果没有设置任何上限，保留为 0（表示不限制）
        if (finalMaxLevelCap == 0 && (maxLevelCap > 0 || tierMaxCap > 0)) {
            finalMaxLevelCap = Math.max(maxLevelCap, tierMaxCap);
        }

        return new MaterialContext(additionStack, universalBonus, affixBonuses,
                tier, tierLevelBonus, minGuaranteedLevel, finalMaxLevelCap);
    }

    /**
     * 标签驱动条目内部类
     */
    private static class TagDrivenEntry {
        final String tag;
        final List<MaterialContext.AffixBonusEntry> bonuses;

        TagDrivenEntry(String tag, List<MaterialContext.AffixBonusEntry> bonuses) {
            this.tag = tag;
            this.bonuses = bonuses;
        }
    }
}
