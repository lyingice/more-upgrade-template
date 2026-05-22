package net.mcreator.mut.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MutTridentStats {

    public record Stats(
            double attackDamage,
            float attackSpeed,
            double throwDamage,
            int enchantmentValue,
            Ingredient repairItem,
            Rarity rarity,
            int durability,
            boolean fireResistant
    ) {}

    // ========== 常量 ==========

    public static final Stats WOODEN  = new Stats(6.0, -2.9F, 5.0,  1,  Ingredient.EMPTY,                               Rarity.COMMON,  125,   false);
    public static final Stats COPPER  = new Stats(6.5, -2.9F, 6.0,  13, Ingredient.of(Items.COPPER_INGOT),               Rarity.UNCOMMON, 215,   false);
    public static final Stats IRON    = new Stats(7.0, -2.9F, 7.0,  14, Ingredient.of(Items.IRON_INGOT),                Rarity.UNCOMMON, 250,   false);
    public static final Stats GOLDEN  = new Stats(8.0, -2.9F, 8.0,  22, Ingredient.of(Items.GOLD_INGOT),                Rarity.UNCOMMON, 375,   false);
    public static final Stats DIAMOND = new Stats(9.0, -2.9F, 10.0, 10, Ingredient.of(Items.DIAMOND),                   Rarity.EPIC,     1031,  false);
    public static final Stats NETHERITE = new Stats(10.0, -2.9F, 11.0, 15, Ingredient.of(Items.NETHERITE_INGOT),        Rarity.EPIC,     1266,  true);
    public static final Stats STEEL   = new Stats(9.5, -2.8F, 10.5, 14, Ingredient.of(MutModItems.STEEL_INGOT),        Rarity.EPIC,     625,   true);
    public static final Stats GILDING = new Stats(10.0, -2.7F, 10.0, 22, Ingredient.of(MutModItems.GILDING_INGOT),      Rarity.EPIC,     1031,  true);
    public static final Stats BLUE_DIAMOND = new Stats(11.0, -2.7F, 12.0, 10, Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT), Rarity.EPIC, 1266, true);
    public static final Stats ADVANCED_STEEL = new Stats(11.0, -2.8F, 12.0, 14, Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT), Rarity.EPIC, 1375, true);
    public static final Stats OBSIDIAN = new Stats(9.5,  -3.0F, 13.0, 1,  Ingredient.of(Items.OBSIDIAN),                      Rarity.EPIC, 1575, true);
    public static final Stats NETHERITE_OBSIDIAN = new Stats(11.0, -3.1F, 16.0, 1,  Ingredient.of(MutModItems.OBSIDIAN_INGOT), Rarity.EPIC, 2075, true);
    public static final Stats CRYING_OBSIDIAN = new Stats(14.0, -3.3F, 20.0, 1,  Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT), Rarity.EPIC, 3075, true);
    public static final Stats NETHER_STAR = new Stats(13.0, -2.9F, 16.0, 22, Ingredient.of(Items.NETHER_STAR),                  Rarity.EPIC, 1750, false);
    public static final Stats DRAGON  = new Stats(16.0, -2.9F, 16.0, 30, Ingredient.of(Items.NETHER_STAR),    Rarity.EPIC, 3194, true);
    public static final Stats WITHER  = new Stats(13.0, -2.7F, 16.0, 22,  Ingredient.of(Items.NETHER_STAR),          Rarity.EPIC, 1750, true);
    public static final Stats NETHERITE_COPPER = new Stats(8.0, -2.9F, 10.5, 8, Ingredient.of(Items.COPPER_BLOCK), Rarity.EPIC, 866, true);
    public static final Stats NETHERITE_EMERALD = new Stats(10.0, -2.9F, 12.0, 9, Ingredient.of(MutModItems.NETHERITE_EMERALD_INGOT), Rarity.EPIC, 1194, true);
    public static final Stats NETHERITE_REDSTONE = new Stats(9.0, -2.5F, 12.0, 20, Ingredient.of(MutModItems.NETHERITE_REDSTONE_INGOT), Rarity.EPIC, 1266, true);
    public static final Stats NETHERITE_AMETHYST = new Stats(9.5, -2.9F, 12.0, 14, Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT), Rarity.EPIC, 950, true);
    public static final Stats AMETHYST = new Stats(7.5, -2.9F, 9.0, 16, Ingredient.of(Items.AMETHYST_SHARD), Rarity.COMMON, 425, false);
    public static final Stats EMERALD = new Stats(8.0, -2.9F, 10.0, 20, Ingredient.of(Items.EMERALD), Rarity.COMMON, 694, false);

    // ========== 映射表 ==========

    private static final Map<Supplier<Item>, Stats> STATS_MAP = new HashMap<>();

    static {
        STATS_MAP.put(MutModItems.WOODEN_TRIDENT, WOODEN);
        STATS_MAP.put(MutModItems.COPPER_TRIDENT, COPPER);
        STATS_MAP.put(MutModItems.IRON_TRIDENT, IRON);
        STATS_MAP.put(MutModItems.GOLDEN_TRIDENT, GOLDEN);
        STATS_MAP.put(MutModItems.DIAMOND_TRIDENT, DIAMOND);
        STATS_MAP.put(MutModItems.NETHERITE_TRIDENT, NETHERITE);
        STATS_MAP.put(MutModItems.STEEL_TRIDENT, STEEL);
        STATS_MAP.put(MutModItems.GILDING_TRIDENT, GILDING);
        STATS_MAP.put(MutModItems.BLUE_DIAMOND_TRIDENT, BLUE_DIAMOND);
        STATS_MAP.put(MutModItems.ADVANCED_STEEL_TRIDENT, ADVANCED_STEEL);
        STATS_MAP.put(MutModItems.OBSIDIAN_TRIDENT, OBSIDIAN);
        STATS_MAP.put(MutModItems.NETHERITE_OBSIDIAN_TRIDENT, NETHERITE_OBSIDIAN);
        STATS_MAP.put(MutModItems.CRYING_OBSIDIAN_TRIDENT, CRYING_OBSIDIAN);
        STATS_MAP.put(MutModItems.NETHER_STAR_TRIDENT, NETHER_STAR);
        STATS_MAP.put(MutModItems.DRAGON_TRIDENT, DRAGON);
        STATS_MAP.put(MutModItems.WITHER_TRIDENT, WITHER);
        STATS_MAP.put(MutModItems.NETHERITE_COPPER_TRIDENT, NETHERITE_COPPER);
        STATS_MAP.put(MutModItems.NETHERITE_EMERALD_TRIDENT, NETHERITE_EMERALD);
        STATS_MAP.put(MutModItems.NETHERITE_REDSTONE_TRIDENT, NETHERITE_REDSTONE);
        STATS_MAP.put(MutModItems.NETHERITE_AMETHYST_TRIDENT, NETHERITE_AMETHYST);
        STATS_MAP.put(MutModItems.AMETHYST_TRIDENT, AMETHYST);
        STATS_MAP.put(MutModItems.EMERALD_TRIDENT, EMERALD);
    }

    public static Stats get(Item item) {
        return STATS_MAP.entrySet().stream()
                .filter(e -> e.getKey().get() == item)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(IRON);
    }
}