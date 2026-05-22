package net.mcreator.mut.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

/**
 * 重锤统计数据配置
 * 风格与 MutCrossbowStats 保持一致
 */
public class MutMaceStats {

    public record Stats(
            int durability,
            int baseDamage,           // 实际伤害 = baseDamage + 1
            float attackSpeed,        // 实际攻速，如 0.6, 0.7, 0.8
            float fallDamageMultiplier,
            int enchantmentValue,
            Rarity rarity,
            Ingredient repairIngredient,
            boolean fireResistant,
            String materialName       // 用于 MutMoreAttributeMaterials 查询
    ) {
        // 获取攻击速度修饰值（用于 AttributeModifier）
        public float getAttackSpeedModifier() {
            return attackSpeed - 4.0f;
        }

        // 工厂方法 - 通用（不可修复）
        public static Stats of(int durability, int baseDamage, float attackSpeed,
                               float fallDamageMultiplier, int enchantmentValue,
                               Rarity rarity, String materialName) {
            return new Stats(durability, baseDamage, attackSpeed, fallDamageMultiplier,
                    enchantmentValue, rarity, Ingredient.EMPTY, false, materialName);
        }

        // 工厂方法 - 可修复，不防火
        public static Stats of(int durability, int baseDamage, float attackSpeed,
                               float fallDamageMultiplier, int enchantmentValue,
                               Rarity rarity, Ingredient repairIngredient, String materialName) {
            return new Stats(durability, baseDamage, attackSpeed, fallDamageMultiplier,
                    enchantmentValue, rarity, repairIngredient, false, materialName);
        }

        // 工厂方法 - 可修复，防火
        public static Stats of(int durability, int baseDamage, float attackSpeed,
                               float fallDamageMultiplier, int enchantmentValue,
                               Rarity rarity, Ingredient repairIngredient,
                               boolean fireResistant, String materialName) {
            return new Stats(durability, baseDamage, attackSpeed, fallDamageMultiplier,
                    enchantmentValue, rarity, repairIngredient, fireResistant, materialName);
        }
    }

    // ========== 预设常量 ==========

    // 基础材料
    public static final Stats WOOD = Stats.of(59, 3, 0.6f, 0.6f, 15, Rarity.COMMON, Ingredient.EMPTY, "wood");
    public static final Stats STONE = Stats.of(131, 3, 0.5f, 0.5f, 5, Rarity.COMMON, Ingredient.of(Items.COBBLESTONE), "stone");
    public static final Stats GOLD = Stats.of(59, 3, 0.6f, 0.6f, 22, Rarity.COMMON, Ingredient.of(Items.GOLD_INGOT), "gold");
    public static final Stats COPPER = Stats.of(195, 4, 0.6f, 0.7f, 13, Rarity.COMMON, Ingredient.of(Items.COPPER_INGOT), "copper");

    // 原版材料
    public static final Stats IRON = Stats.of(250, 4, 0.6f, 0.8f, 14, Rarity.COMMON, Ingredient.of(Items.IRON_INGOT), "iron");
    public static final Stats DIAMOND = Stats.of(1561, 5, 0.6f, 1.0f, 10, Rarity.COMMON, Ingredient.of(Items.DIAMOND), "diamond");
    public static final Stats NETHERITE = Stats.of(2031, 6, 0.6f, 1.1f, 15, Rarity.COMMON, Ingredient.of(Items.NETHERITE_INGOT), true, "netherite");

    // 自定义材料
    public static final Stats STEEL = Stats.of(875, 5, 0.7f, 1.15f, 12, Rarity.EPIC, Ingredient.of(MutModItems.STEEL_INGOT.get()), true, "steel");
    public static final Stats GILDING = Stats.of(875, 5, 0.8f, 1.25f, 22, Rarity.EPIC, Ingredient.of(MutModItems.GILDING_INGOT.get()), true, "gilding");
    public static final Stats BLUE_DIAMOND = Stats.of(1516, 7, 0.8f, 1.25f, 18, Rarity.EPIC, Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT.get()), true, "blue_diamond");
    public static final Stats ADVANCED_STEEL = Stats.of(1625, 7, 0.7f, 1.30f, 15, Rarity.EPIC, Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT.get()), true, "advanced_steel");

    // 特殊材料
    // 黑曜石重锤
    public static final Stats OBSIDIAN = Stats.of(1825, 5, 0.5f, 1.2f, 1, Rarity.COMMON,
            Ingredient.of(Items.OBSIDIAN), false, "obsidian");

    // 下界合金黑曜石重锤
    public static final Stats NETHERITE_OBSIDIAN = Stats.of(2325, 7, 0.4f, 1.5f, 1, Rarity.UNCOMMON,
            Ingredient.of(MutModItems.OBSIDIAN_INGOT.get()), true, "netherite_obsidian");

    // 悲悯重锤（Crying Obsidian）
    public static final Stats CRYING_OBSIDIAN = Stats.of(3325, 10, 0.2f, 2.0f, 1, Rarity.EPIC,
            Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT.get()), true, "crying_obsidian");

    // 下界之星重锤
    public static final Stats NETHER_STAR = Stats.of(2000, 9, 0.6f, 1.5f, 22, Rarity.EPIC,
            Ingredient.of(Items.NETHER_STAR), true, "nether_star");

    // 凋零重锤
    public static final Stats WITHER = Stats.of(2000, 9, 0.8f, 1.5f, 22, Rarity.EPIC,
            Ingredient.of(Items.NETHER_STAR), true, "wither");

    // 龙重锤
    public static final Stats DRAGON = Stats.of(3444, 12, 0.6f, 1.5f, 30, Rarity.EPIC,
            Ingredient.of(Items.NETHER_STAR), true, "dragon");

    // 下界合金铜重锤
    public static final Stats NETHERITE_COPPER = Stats.of(1116, 5, 0.6f, 1.15f, 15, Rarity.UNCOMMON,
            Ingredient.of(Items.COPPER_BLOCK), true, "netherite_copper");

    // 下界合金绿宝石重锤
    public static final Stats NETHERITE_EMERALD = Stats.of(1444, 7, 0.6f, 1.2f, 18, Rarity.UNCOMMON,
            Ingredient.of(MutModItems.NETHERITE_EMERALD_INGOT.get()), true, "netherite_emerald");

    // 下界合金红石重锤
    public static final Stats NETHERITE_REDSTONE = Stats.of(1516, 6, 1.0f, 1.2f, 20, Rarity.UNCOMMON,
            Ingredient.of(MutModItems.NETHERITE_REDSTONE_INGOT.get()), true, "netherite_redstone");

    // 下界合金紫水晶重锤
    public static final Stats NETHERITE_AMETHYST = Stats.of(1200, 6, 0.6f, 1.2f, 16, Rarity.UNCOMMON,
            Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT.get()), true, "netherite_amethyst");
    // 绿宝石重锤
    public static final Stats EMERALD = Stats.of(944, 5, 0.6f, 0.9f, 18, Rarity.COMMON,
            Ingredient.of(Items.EMERALD), false, "emerald");

    // 紫水晶重锤
    public static final Stats AMETHYST = Stats.of(675, 4, 0.6f, 0.8f, 16, Rarity.COMMON,
            Ingredient.of(Items.AMETHYST_SHARD), false, "amethyst");
    public static final Stats DEFAULT = IRON;

    // ========== Map：Item → Stats ==========
    private static final Map<Item, Stats> STATS_MAP = new HashMap<>();

    public static void register(Item mace, Stats stats) {
        STATS_MAP.put(mace, stats);
    }

    public static Stats get(Item mace) {
        return STATS_MAP.getOrDefault(mace, DEFAULT);
    }

    // ========== 便捷方法 ==========
    public static int durability(Item mace) { return get(mace).durability(); }
    public static int baseDamage(Item mace) { return get(mace).baseDamage(); }
    public static float attackSpeed(Item mace) { return get(mace).attackSpeed(); }
    public static float getAttackSpeedModifier(Item mace) { return get(mace).getAttackSpeedModifier(); }
    public static float fallDamageMultiplier(Item mace) { return get(mace).fallDamageMultiplier(); }
    public static int enchantmentValue(Item mace) { return get(mace).enchantmentValue(); }
    public static Rarity rarity(Item mace) { return get(mace).rarity(); }
    public static Ingredient repairIngredient(Item mace) { return get(mace).repairIngredient(); }
    public static boolean fireResistant(Item mace) { return get(mace).fireResistant(); }
    public static String materialName(Item mace) { return get(mace).materialName(); }
}