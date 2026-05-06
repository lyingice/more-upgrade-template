package net.mcreator.mut.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;

/**
 * 弩的统一数值配置库
 * 集中管理每把弩的所有基础属性
 */
public class MutCrossbowStats {

    /**
     * 单把弩的全部配置数据
     */
    public record Stats(
            double maxChargeTime,      // 最大蓄力时间（秒）
            float projectileSpeed,     // 弹射物飞行速度
            int defaultLoadCount,      // 默认装填数量（不配置时通过工厂方法默认为 1）
            int durability,            // 最大耐久
            Rarity rarity,            // 稀有度
            Ingredient repairItem      // 修复物品
    ) {
        /** 装填数量是否需要在 tooltip 中显示 */
        public boolean showLoadCount() {
            return defaultLoadCount > 1;
        }

        // ========== 工厂方法：省略装填数量时默认为 1 ==========

        /** 全参数（很少用） */
        public static Stats of(double maxChargeTime, float projectileSpeed, int defaultLoadCount,
                               int durability, Rarity rarity, Ingredient repairItem) {
            return new Stats(maxChargeTime, projectileSpeed, defaultLoadCount, durability, rarity, repairItem);
        }

        /** 省略装填数量，默认 1 */
        public static Stats of(double maxChargeTime, float projectileSpeed,
                               int durability, Rarity rarity, Ingredient repairItem) {
            return new Stats(maxChargeTime, projectileSpeed, 1, durability, rarity, repairItem);
        }
    }

    public static final Stats DEFAULT = Stats.of(
            1.25F,          // 蓄力时间
            3.15F,          // 弹射物速度
            1,              // 默认装填 1
            465,            // 耐久
            Rarity.COMMON,
            Ingredient.EMPTY
    );

    private static final Map<Item, Stats> STATS_MAP = new HashMap<>();

    static {
        // ========== 示例：省略装填数量则默认 1 ==========
        // register(MutModItems.IRON_CROSSBOW.get(),   Stats.of(1.5F, 3.5F, 500, Rarity.COMMON,    Ingredient.of(Items.IRON_INGOT)));
        // register(MutModItems.DIAMOND_CROSSBOW.get(), Stats.of(1.0F, 3.8F, 800, Rarity.UNCOMMON,  Ingredient.of(Items.DIAMOND)));

        // ========== 特殊弩显式声明装填数量 ==========
        // register(MutModItems.HEAVY_CROSSBOW.get(),    Stats.of(2.5F, 4.5F, 3, 600, Rarity.RARE,   Ingredient.of(Items.NETHERITE_INGOT)));
        // register(MutModItems.REPEATER_CROSSBOW.get(), Stats.of(0.8F, 2.8F, 6, 400, Rarity.EPIC,   Ingredient.of(Items.COPPER_INGOT)));
    }

    public static void register(Item crossbow, Stats stats) {
        STATS_MAP.put(crossbow, stats);
    }

    public static Stats get(Item crossbow) {
        return STATS_MAP.getOrDefault(crossbow, DEFAULT);
    }

    // ========== 便捷方法 ==========

    public static double chargeTime(Item crossbow) {
        return get(crossbow).maxChargeTime();
    }

    public static float projectileSpeed(Item crossbow) {
        return get(crossbow).projectileSpeed();
    }

    public static int defaultLoadCount(Item crossbow) {
        return get(crossbow).defaultLoadCount();
    }

    public static int durability(Item crossbow) {
        return get(crossbow).durability();
    }

    public static Rarity rarity(Item crossbow) {
        return get(crossbow).rarity();
    }

    public static Ingredient repairItem(Item crossbow) {
        return get(crossbow).repairItem();
    }
}