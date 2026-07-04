package net.mcreator.mut.compat.jei;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SuperSmithingTableRecipe {
    private static final ResourceLocation AFFIX_MATERIAL_TAG = ResourceLocation.fromNamespaceAndPath("mut", "affix_material");
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private final ItemStack template, base, addition, result;
    private final String bonusDescription;
    private final double fixedProbability;
    private final int maxLevel, minGuaranteedLevel, maxLevelCap;

    public SuperSmithingTableRecipe(ItemStack template, ItemStack base, ItemStack addition, ItemStack result,
                                     String bonusDescription, double fixedProbability, int maxLevel,
                                     int minGuaranteedLevel, int maxLevelCap) {
        this.template = template; this.base = base; this.addition = addition; this.result = result;
        this.bonusDescription = bonusDescription; this.fixedProbability = fixedProbability;
        this.maxLevel = maxLevel; this.minGuaranteedLevel = minGuaranteedLevel; this.maxLevelCap = maxLevelCap;
    }
    public ItemStack getTemplate() { return template; }
    public ItemStack getBase() { return base; }
    public ItemStack getAddition() { return addition; }
    public ItemStack getResult() { return result; }
    public String getBonusDescription() { return bonusDescription; }
    public double getFixedProbability() { return fixedProbability; }
    public int getMaxLevel() { return maxLevel; }
    public int getMinGuaranteedLevel() { return minGuaranteedLevel; }
    public int getMaxLevelCap() { return maxLevelCap; }

    public static List<SuperSmithingTableRecipe> generateAll() {
        List<SuperSmithingTableRecipe> recipes = new ArrayList<>();
        ItemStack template = new ItemStack(MutModItems.SUPER_UPGRADE_SMITHING_TEMPLATE.get());
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        MaterialBonusData bonusData = loadMaterialBonusData();
        var affixMaterialTag = ItemTags.create(AFFIX_MATERIAL_TAG);
        for (var itemHolder : BuiltInRegistries.ITEM.holders().toList()) {
            ItemStack materialStack = new ItemStack(itemHolder.value());
            if (materialStack.is(affixMaterialTag)) {
                String itemId = BuiltInRegistries.ITEM.getKey(itemHolder.value()).toString();
                MaterialBonusData.MaterialInfo info = bonusData.find(itemId);
                String description; double fixedProb = 0; int maxLv = 6; int minGuaranteed = 0; int maxCap = 0;
                if (info != null) {
                    minGuaranteed = info.minGuaranteedLevel;
                    maxCap = Math.max(info.maxLevelCap, info.tierMaxLevelCap);
                    if (!info.directedAffixes.isEmpty()) {
                        var first = info.directedAffixes.get(0);
                        description = "§a→ " + first.targetAffix + " §7概率" + String.format("%.0f%%", first.fixedProb * 100);
                        fixedProb = first.fixedProb; maxLv = first.maxLevel;
                    } else if (info.enchantBonus > 0) {
                        description = "§b通用材料 §7附魔+" + info.enchantBonus;
                    } else { description = "§7无特殊加成"; }
                    if (minGuaranteed > 0) description += " §a保底≥Lv" + minGuaranteed;
                    if (maxCap > 0) description += " §c上限≤Lv" + maxCap;
                } else { description = "§7无特殊加成"; }
                recipes.add(new SuperSmithingTableRecipe(template.copy(), diamondSword.copy(), materialStack.copy(), diamondSword.copy(),
                        description, fixedProb, maxLv, minGuaranteed, maxCap));
            }
        }
        return recipes;
    }

    @SuppressWarnings("unchecked")
    private static MaterialBonusData loadMaterialBonusData() {
        MaterialBonusData data = new MaterialBonusData();
        try (var in = SuperSmithingTableRecipe.class.getClassLoader().getResourceAsStream("data/mut/affix/material_bonuses.json")) {
            if (in == null) return data;
            var json = GSON.fromJson(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8), java.util.Map.class);
            if (json == null) return data;
            var universal = (java.util.List<java.util.Map<String, Object>>) json.get("universal_materials");
            if (universal != null) {
                for (var um : universal) {
                    String item = (String) um.get("item"); if (item == null) continue;
                    int enchant = ((Number) um.getOrDefault("enchant_bonus", 0)).intValue();
                    int minGuaranteed = ((Number) um.getOrDefault("min_guaranteed_level", 0)).intValue();
                    int maxCap = ((Number) um.getOrDefault("max_level_cap", 0)).intValue();
                    data.addUniversal(item, enchant, minGuaranteed, maxCap);
                }
            }
            var directed = (java.util.List<java.util.Map<String, Object>>) json.get("directed_materials");
            if (directed != null) {
                for (var dm : directed) {
                    String item = (String) dm.get("item"); if (item == null) continue;
                    var bonuses = (java.util.List<java.util.Map<String, Object>>) dm.get("affix_bonuses");
                    if (bonuses == null) continue;
                    for (var ab : bonuses) {
                        String target = (String) ab.get("target_affix");
                        double fixedProb = ((Number) ab.getOrDefault("fixed_probability", 0)).doubleValue();
                        int maxLevel = ((Number) ab.getOrDefault("max_level", 6)).intValue();
                        data.addDirected(item, target, fixedProb, maxLevel);
                    }
                }
            }
        } catch (Exception e) { }
        return data;
    }

    private static class MaterialBonusData {
        static class DirectedAffix {
            final String targetAffix; final double fixedProb; final int maxLevel;
            DirectedAffix(String targetAffix, double fixedProb, int maxLevel) { this.targetAffix = targetAffix; this.fixedProb = fixedProb; this.maxLevel = maxLevel; }
        }
        static class MaterialInfo {
            int enchantBonus = 0; int minGuaranteedLevel = 0; int maxLevelCap = 0; int tierMaxLevelCap = 0;
            final List<DirectedAffix> directedAffixes = new ArrayList<>();
        }
        private final java.util.Map<String, MaterialInfo> data = new java.util.HashMap<>();
        void addUniversal(String item, int enchant, int minG, int maxC) {
            MaterialInfo info = data.computeIfAbsent(item, k -> new MaterialInfo());
            info.enchantBonus = enchant; info.minGuaranteedLevel = minG; info.maxLevelCap = maxC;
        }
        void addDirected(String item, String target, double fixedProb, int maxLevel) {
            data.computeIfAbsent(item, k -> new MaterialInfo()).directedAffixes.add(new DirectedAffix(target, fixedProb, maxLevel));
        }
        MaterialInfo find(String itemId) { return data.get(itemId); }
    }
}
