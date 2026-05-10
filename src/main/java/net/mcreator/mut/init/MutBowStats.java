package net.mcreator.mut.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 弓的统计数据配置类
 * 集中管理所有弓的属性配置
 */
public class MutBowStats {

    /**
     * 弓的配置记录
     * @param maxDrawDuration    最大拉弓时间（tick，20 tick = 1秒）
     * @param durability         耐久度
     * @param enchantmentValue   附魔能力
     * @param rarity             稀有度
     * @param damageBonus        额外伤害加成（会加到原版2.0上）
     * @param repairIngredient   修复材料
     * @param fireResistant      是否防火
     */
    public record BowConfig(
            int maxDrawDuration,
            int durability,
            int enchantmentValue,
            Rarity rarity,
            double damageBonus,
            Supplier<Ingredient> repairIngredient,
            boolean fireResistant
    ) {}

    // ========== 配置存储 ==========
    private static final Map<Item, BowConfig> BOW_CONFIGS = new HashMap<>();

    // ========== 预设材料配置 ==========

    // 基础材料
    public static final BowConfig WOOD = new BowConfig(20, 59, 4, Rarity.COMMON, 0.0,
            () -> Ingredient.of(Items.OAK_PLANKS), false);

    public static final BowConfig STONE = new BowConfig(20, 131, 5, Rarity.COMMON, 0.2,
            () -> Ingredient.of(Items.COBBLESTONE), false);

    // 原版材料
    public static final BowConfig IRON = new BowConfig(20, 484, 14, Rarity.COMMON, 0.5,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig GOLD = new BowConfig(10, 69, 22, Rarity.COMMON, 0.0,
            () -> Ingredient.of(Items.GOLD_INGOT), false);

    public static final BowConfig DIAMOND = new BowConfig(20, 684, 14, Rarity.COMMON, 1.0,
            () -> Ingredient.of(Items.DIAMOND), false);

    public static final BowConfig NETHERITE = new BowConfig(20, 784, 15, Rarity.COMMON, 1.5,
            () -> Ingredient.of(Items.NETHERITE_INGOT), true);

    // 自定义材料
    public static final BowConfig STEEL = new BowConfig(20, 759, 14, Rarity.COMMON, 1.25,
            () -> Ingredient.of(MutModItems.STEEL_INGOT.get()), false);

    public static final BowConfig GILDING = new BowConfig(10, 1165, 14, Rarity.COMMON, 1.0,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig ADVANCED_STEEL = new BowConfig(15, 1509, 14, Rarity.COMMON, 1.75,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig BLUE_DIAMOND = new BowConfig(15, 1400, 14, Rarity.COMMON, 1.5,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig NETHER_STAR = new BowConfig(20, 1884, 22, Rarity.EPIC, 1.5,
            () -> Ingredient.of(Items.NETHER_STAR), false);

    public static final BowConfig OBSIDIAN = new BowConfig(25, 1709, 14, Rarity.COMMON, 2.0,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig NETHERITE_OBSIDIAN = new BowConfig(25, 2209, 14, Rarity.COMMON, 3.0,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig CRYING_OBSIDIAN = new BowConfig(25, 3209, 14, Rarity.EPIC, 4.0,
            () -> Ingredient.of(Items.IRON_INGOT), false);

    public static final BowConfig COPPER = new BowConfig(18, 479, 8, Rarity.COMMON, 0.5,
            () -> Ingredient.of(Items.COPPER_INGOT), false);

    public static final BowConfig NETHERITE_COPPER = new BowConfig(16, 1000, 8, Rarity.COMMON, 1.25,
            () -> Ingredient.of(Blocks.COPPER_BLOCK), true);

    public static final BowConfig DRAGON = new BowConfig(20,3328,30,Rarity.EPIC,2.5,
            () -> Ingredient.of(Items.NETHER_STAR),true);

    // ========== 注册方法 ==========

    /**
     * 注册弓的配置
     */
    public static void register(Item bow, BowConfig config) {
        BOW_CONFIGS.put(bow, config);
    }

    /**
     * 获取弓的最大拉弓时间
     */
    public static int getMaxDrawDuration(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).maxDrawDuration();
    }

    /**
     * 获取弓的耐久度
     */
    public static int getDurability(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).durability();
    }

    /**
     * 获取弓的附魔能力
     */
    public static int getEnchantmentValue(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).enchantmentValue();
    }

    /**
     * 获取弓的稀有度
     */
    public static Rarity getRarity(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).rarity();
    }

    /**
     * 获取弓的额外伤害加成
     */
    public static double getDamageBonus(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).damageBonus();
    }

    /**
     * 获取弓的修复材料
     */
    public static Ingredient getRepairIngredient(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).repairIngredient().get();
    }

    /**
     * 是否防火
     */
    public static boolean isFireResistant(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON).fireResistant();
    }

    /**
     * 获取完整配置
     */
    public static BowConfig getConfig(Item bow) {
        return BOW_CONFIGS.getOrDefault(bow, IRON);
    }
}