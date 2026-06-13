package net.mcreator.mut.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class MutCrossbowStats {

    public record Stats(
            double maxChargeTime,
            float projectileSpeed,
            int defaultLoadCount,
            int durability,
            Rarity rarity,
            Ingredient repairItem,
            int enchantmentValue,
            boolean fireResistant
    ) {
        public boolean showLoadCount() {
            return defaultLoadCount > 1;
        }

        // ========== 工厂方法（给 Map 用） ==========

        public static Stats of(double maxChargeTime, float projectileSpeed, int defaultLoadCount,
                               int durability, Rarity rarity, Ingredient repairItem,
                               int enchantmentValue, boolean fireResistant) {
            return new Stats(maxChargeTime, projectileSpeed, defaultLoadCount, durability, rarity, repairItem, enchantmentValue, fireResistant);
        }

        public static Stats of(double maxChargeTime, float projectileSpeed,
                               int durability, Rarity rarity, Ingredient repairItem,
                               int enchantmentValue) {
            return new Stats(maxChargeTime, projectileSpeed, 1, durability, rarity, repairItem, enchantmentValue, false);
        }

        public static Stats of(double maxChargeTime, float projectileSpeed, int defaultLoadCount,
                               int durability, Rarity rarity, Ingredient repairItem,
                               int enchantmentValue) {
            return new Stats(maxChargeTime, projectileSpeed, defaultLoadCount, durability, rarity, repairItem, enchantmentValue, true);
        }
    }

    // ========== 预设常量（和弓系统完全一致，不依赖 .get()） ==========

    public static final Stats COPPER             = Stats.of(1.25F, 3.5F,  590,  Rarity.COMMON, Ingredient.of(Items.COPPER_INGOT),            13);
    public static final Stats IRON               = Stats.of(1.25F, 3.5F,  590,  Rarity.COMMON, Ingredient.of(Items.IRON_INGOT),             14);
    public static final Stats GOLDEN             = Stats.of(1.0F,  3.5F,  715,  Rarity.COMMON, Ingredient.of(Items.GOLD_INGOT),             22);
    public static final Stats DIAMOND            = Stats.of(1.25F, 4.0F,  1246, Rarity.COMMON, Ingredient.of(Items.DIAMOND),                10);
    public static final Stats NETHERITE          = Stats.of(1.25F, 4.5F,1,  1481, Rarity.COMMON, Ingredient.of(Items.NETHERITE_INGOT),        15, true);
    public static final Stats STEEL              = Stats.of(1.1F,  4.5F,  1,840,  Rarity.COMMON, Ingredient.of(MutModItems.STEEL_INGOT),      15, true);
    public static final Stats GILDING            = Stats.of(0.9F,  4.5F,  1,1246, Rarity.COMMON, Ingredient.of(MutModItems.GILDING_INGOT),    22, true);
    public static final Stats BLUE_DIAMOND       = Stats.of(1.1F,  5.0F,  1,1481, Rarity.COMMON, Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT), 18, true);
    public static final Stats ADVANCED_STEEL     = Stats.of(1.1F,  5.0F,  1,1590, Rarity.COMMON, Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT), 15, true);
    public static final Stats OBSIDIAN           = Stats.of(1.5F,  4.5F,  3, 1790, Rarity.COMMON, Ingredient.of(Items.OBSIDIAN), 1, true);
    public static final Stats NETHERITE_OBSIDIAN = Stats.of(1.75F, 4.8F,  6, 2290, Rarity.COMMON, Ingredient.of(MutModItems.OBSIDIAN_INGOT), 1, true);
    public static final Stats CRYING_OBSIDIAN    = Stats.of(2.0F,  5.0F, 9, 3290, Rarity.EPIC,   Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT), 1, true);
    public static final Stats NETHER_STAR        = Stats.of(1.25F, 5.0F,  3, 1965, Rarity.EPIC,   Ingredient.of(Items.NETHER_STAR),           22, true);
    public static final Stats WITHER             = Stats.of(1.25F, 5.0F,  3, 1965, Rarity.EPIC,   Ingredient.of(Items.NETHER_STAR),           22, true);
    public static final Stats DRAGON             = Stats.of(1.25F, 5.0F,  3, 3409, Rarity.EPIC,   Ingredient.of(Items.NETHER_STAR),           30, true);
    public static final Stats NETHERITE_EMERALD   = Stats.of(1.25F, 4.5F, 1409, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_EMERALD_INGOT), 20);
    public static final Stats NETHERITE_REDSTONE  = Stats.of(1.0F, 4.7F, 1481, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_REDSTONE_INGOT), 15);
    public static final Stats NETHERITE_AMETHYST  = Stats.of(1.25F, 4.5F, 1165, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT), 16);
    public static final Stats NETHERITE_COPPER    = Stats.of(1.25F, 4.5F, 1081, Rarity.COMMON, Ingredient.of(Items.COPPER_BLOCK), 13);
    public static final Stats EMERALD             = Stats.of(1.25F, 4.0F, 909,  Rarity.COMMON, Ingredient.of(Items.EMERALD),             20);
    public static final Stats AMETHYST            = Stats.of(1.25F, 3.4F, 640, Rarity.COMMON, Ingredient.of(Items.AMETHYST_SHARD),       16);
    public static final Stats LAPIS_LAZULI = Stats.of(1.25F,3.3F,1,565,Rarity.COMMON,Ingredient.of(Items.LAPIS_LAZULI),30);
    public static final Stats NETHERITE_LAPIS_LAZULI = Stats.of(1.25F,4.5F,1,965,Rarity.COMMON,Ingredient.of(MutModItems.NETHERITE_LAPIS_LAZULI_INGOT),60);
    public static final Stats POISON_STEEL = Stats.of(1.25F,5F,3,1481,Rarity.UNCOMMON,Ingredient.of(MutModItems.POSITION_STEEL_INGOT),15);
    public static final Stats FLAME_GOLD = Stats.of(1.25F,5F,3,1481,Rarity.UNCOMMON,Ingredient.of(MutModItems.FLAME_GOLD_INGOT),25);
    public static final Stats ECHOITE = Stats.of(1.25F,5F,6,2496,Rarity.EPIC,Ingredient.of(MutModItems.ECHOITE_INGOT),30);
    public static final Stats THUNDER_COPPER = Stats.of(1.25F,5F,3,1481,Rarity.UNCOMMON,Ingredient.of(MutModItems.THUNDER_COPPER_STAR),1);
    public static final Stats UNCANNY_AMETHYST = Stats.of(1.25F,5F,3,1865,Rarity.UNCOMMON,Ingredient.of(MutModItems.UNCANNY_AMETHYST_STAR),16);
    public static final Stats DEFAULT             = IRON;

    // ========== Map：Item → Stats ==========
    private static final Map<Item, Stats> STATS_MAP = new HashMap<>();

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

    public static int enchantmentValue(Item crossbow) {
        return get(crossbow).enchantmentValue();
    }

    public static boolean fireResistant(Item crossbow) {
        return get(crossbow).fireResistant();
    }
}