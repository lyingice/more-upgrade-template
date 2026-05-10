package net.mcreator.mut.init;

import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;

/**
 * 弓的额外伤害注册类
 * 只负责存储和提供每种弓的额外伤害加成
 * 该配置已废弃,0.2.0以后将逐步移除
 */
public class MutBowDamage {

    private static final Map<Item, Double> BOW_DAMAGE_BONUS = new HashMap<>();

    static {
        // 注册铁弓：额外 +0.5 伤害
        register(MutModItems.IRON_BOW.get(), 0.5);
        register(MutModItems.DIAMOND_BOW.get(), 1.0);
        register(MutModItems.GOLDEN_BOW.get(), 0.0);
        register(MutModItems.NETHERITE_BOW.get(), 1.5);
        register(MutModItems.STEEL_BOW.get(), 1.25);
        register(MutModItems.GILDING_BOW.get(), 1.0);
        register(MutModItems.BLUE_DIAMOND_BOW.get(), 1.5);
        register(MutModItems.ADVANCED_STEEL_BOW.get(), 1.75);
        register(MutModItems.NETHER_STAR_BOW.get(), 1.5);
        register(MutModItems.OBSIDIAN_BOW.get(), 2.0);
        register(MutModItems.NETHERITE_OBSIDIAN_BOW.get(), 3.0);
        register(MutModItems.CRYING_OBSIDIAN_BOW.get(), 4.0);
        register(MutModItems.COPPER_BOW.get(), 0.5);
        register(MutModItems.NETHERITE_COPPER_BOW.get(), 1.25);
        // ========== 在这里添加其他弓 ==========
        // register(MutModItems.X_BOW.get(), 1.5);
    }

    /**
     * 注册弓的额外伤害
     */
    public static void register(Item bow, double bonus) {
        BOW_DAMAGE_BONUS.put(bow, bonus);
    }

    /**
     * 获取弓的额外伤害（没有注册则返回 0）
     */
    public static double get(Item bow) {
        return BOW_DAMAGE_BONUS.getOrDefault(bow, 0.0);
    }
}