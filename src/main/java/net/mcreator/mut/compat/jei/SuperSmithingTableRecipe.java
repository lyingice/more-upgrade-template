package net.mcreator.mut.compat.jei;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 超级锻造台 JEI 配方数据类
 * 表示一个配方示例：展示槽位关系和可用材料
 *
 * generateAll() 直接从 JSON 资源读取材料数据，
 * 避免 JEI 注册时序早于数据包加载器的问题。
 */
public class SuperSmithingTableRecipe {

    private static final ResourceLocation AFFIX_MATERIAL_TAG =
            ResourceLocation.fromNamespaceAndPath("mut", "affix_material");

    private static final Gson GSON = new GsonBuilder().setLenient().create();

    private final ItemStack template;       // 槽位0: 超级升级锻造模板
    private final ItemStack base;           // 槽位1: 示例可附魔物品
    private final ItemStack addition;       // 槽位2: 示例附魔材料
    private final ItemStack result;         // 槽位3: 输出（随机词缀预览）

    // JEI 显示额外信息
    private final String bonusDescription;  // 材料加成描述文本
    private final double affixWeightMult;   // 词缀权重倍率（如有定向）
    private final double levelWeightBonus;  // 等级权重加成

    public SuperSmithingTableRecipe(ItemStack template, ItemStack base,
                                    ItemStack addition, ItemStack result,
                                    String bonusDescription,
                                    double affixWeightMult,
                                    double levelWeightBonus) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.bonusDescription = bonusDescription;
        this.affixWeightMult = affixWeightMult;
        this.levelWeightBonus = levelWeightBonus;
    }

    public ItemStack getTemplate() { return template; }
    public ItemStack getBase() { return base; }
    public ItemStack getAddition() { return addition; }
    public ItemStack getResult() { return result; }
    public String getBonusDescription() { return bonusDescription; }
    public double getAffixWeightMult() { return affixWeightMult; }
    public double getLevelWeightBonus() { return levelWeightBonus; }

    // ========== 配方生成 ==========

    /**
     * 生成所有展示用的配方示例
     * 直接从 classpath 加载 material_bonuses.json，
     * 避免 JEI 注册时序早于数据包加载器的问题。
     */
    public static List<SuperSmithingTableRecipe> generateAll() {
        List<SuperSmithingTableRecipe> recipes = new ArrayList<>();
        ItemStack template = new ItemStack(MutModItems.SUPER_UPGRADE_SMITHING_TEMPLATE.get());
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);

        // 预加载材料加成映射，直接从 JSON 读取（不依赖 AffixDataLoader 的数据包加载时序）
        MaterialBonusData bonusData = loadMaterialBonusData();

        // 遍历所有注册物品，筛选出在 affix_material 标签中的
        var affixMaterialTag = ItemTags.create(AFFIX_MATERIAL_TAG);
        for (var itemHolder : BuiltInRegistries.ITEM.holders().toList()) {
            ItemStack materialStack = new ItemStack(itemHolder.value());
            if (materialStack.is(affixMaterialTag)) {
                String itemId = BuiltInRegistries.ITEM.getKey(itemHolder.value()).toString();

                // 从预加载数据中查找该材料的加成
                MaterialBonusData.MaterialInfo info = bonusData.find(itemId);

                String description;
                double affixMult = 1.0;
                double levelBonus = 0.0;

                if (info != null) {
                    levelBonus = info.universalLevelBonus;

                    if (!info.directedAffixes.isEmpty()) {
                        var first = info.directedAffixes.get(0);
                        description = "§a→ " + first.targetAffix
                                + " §7×" + String.format("%.1f", first.affixWeightMult)
                                + " 等级+" + String.format("%.0f%%", first.levelBonus * 100);
                        affixMult = first.affixWeightMult;
                        levelBonus = first.levelBonus;
                    } else if (levelBonus > 0) {
                        description = "§b通用材料 §7等级权重+" + String.format("%.0f%%", levelBonus * 100);
                    } else {
                        // 检查是否在 material_tiers 中有定义
                        int tier = info.materialTier;
                        double tierBonus = info.tierLevelBonus;
                        if (tier > 0) {
                            description = "§7品质T" + tier + " §b+" + String.format("%.0f%%", tierBonus * 100) + "权重";
                        } else {
                            description = "§7无特殊加成";
                        }
                    }
                } else {
                    description = "§7无特殊加成";
                }

                recipes.add(new SuperSmithingTableRecipe(
                        template.copy(), diamondSword.copy(),
                        materialStack.copy(), diamondSword.copy(),
                        description, affixMult, levelBonus
                ));
            }
        }

        return recipes;
    }

    /**
     * 从 classpath 直接加载 material_bonuses.json
     */
    private static MaterialBonusData loadMaterialBonusData() {
        MaterialBonusData data = new MaterialBonusData();

        try (var in = SuperSmithingTableRecipe.class.getClassLoader()
                .getResourceAsStream("data/mut/affix/material_bonuses.json")) {
            if (in == null) return data;

            var json = GSON.fromJson(
                    new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8),
                    java.util.Map.class
            );

            if (json == null) return data;
        // 解析通用材料
            var universal = (java.util.List<java.util.Map<String, Object>>) json.get("universal_materials");
            if (universal != null) {
                for (var um : universal) {
                    String item = (String) um.get("item");
                    if (item == null) continue;
                    double bonus = ((Number) um.getOrDefault("level_weight_bonus", 0)).doubleValue();
                    data.addUniversal(item, bonus);
                }
            }

            // 解析定向材料
            var directed = (java.util.List<java.util.Map<String, Object>>) json.get("directed_materials");
            if (directed != null) {
                for (var dm : directed) {
                    String item = (String) dm.get("item");
                    if (item == null) continue;
                    var bonuses = (java.util.List<java.util.Map<String, Object>>) dm.get("affix_bonuses");
                    if (bonuses == null) continue;
                    for (var ab : bonuses) {
                        String target = (String) ab.get("target_affix");
                        double weightMult = ((Number) ab.getOrDefault("affix_weight_multiplier", 1.0)).doubleValue();
                        double levelBonus = ((Number) ab.getOrDefault("level_weight_bonus", 0)).doubleValue();
                        int maxLevel = ((Number) ab.getOrDefault("max_level", 6)).intValue();
                        data.addDirected(item, target, weightMult, levelBonus, maxLevel);
                    }
                }
            }

            // 解析品质层级
            var tiers = (java.util.List<java.util.Map<String, Object>>) json.get("material_tiers");
            if (tiers != null) {
                for (var mt : tiers) {
                    int tier = ((Number) mt.get("tier")).intValue();
                    double bonus = ((Number) mt.get("level_bonus")).doubleValue();
                    var items = (java.util.List<String>) mt.get("items");
                    if (items != null) {
                        for (String item : items) {
                            data.addTier(item, tier, bonus);
                        }
                    }
                }
            }

        } catch (Exception e) {
            // 文件不存在或解析失败，返回空数据
        }

        return data;
    }

    /**
     * 材料加成数据（内部类，直接从 JSON 解析）
     */
    private static class MaterialBonusData {
        static class DirectedAffix {
            final String targetAffix;
            final double affixWeightMult;
            final double levelBonus;
            final int maxLevel;

            DirectedAffix(String targetAffix, double affixWeightMult, double levelBonus, int maxLevel) {
                this.targetAffix = targetAffix;
                this.affixWeightMult = affixWeightMult;
                this.levelBonus = levelBonus;
                this.maxLevel = maxLevel;
            }
        }

        static class MaterialInfo {
            double universalLevelBonus = 0;
            int materialTier = 0;
            double tierLevelBonus = 0;
            final List<DirectedAffix> directedAffixes = new ArrayList<>();
        }

        private final java.util.Map<String, MaterialInfo> data = new java.util.HashMap<>();

        void addUniversal(String item, double bonus) {
            data.computeIfAbsent(item, k -> new MaterialInfo()).universalLevelBonus = bonus;
        }

        void addDirected(String item, String target, double weightMult, double levelBonus, int maxLevel) {
            data.computeIfAbsent(item, k -> new MaterialInfo())
                    .directedAffixes.add(new DirectedAffix(target, weightMult, levelBonus, maxLevel));
        }

        void addTier(String item, int tier, double bonus) {
            var info = data.computeIfAbsent(item, k -> new MaterialInfo());
            info.materialTier = tier;
            info.tierLevelBonus = bonus;
        }

        MaterialInfo find(String itemId) {
            return data.get(itemId);
        }
    }
}
