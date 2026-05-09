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
            Rarity rarity
    ) {}

    // ========== 常量必须先于 static 块 ==========

    public static final Stats WOODEN  = new Stats(6.0, -2.9F, 5.0,  1,  Ingredient.EMPTY,                               Rarity.COMMON);
    public static final Stats COPPER  = new Stats(6.5, -2.9F, 6.0,  13, Ingredient.of(Items.COPPER_INGOT),               Rarity.UNCOMMON);
    public static final Stats IRON    = new Stats(7.0, -2.9F, 7.0,  14, Ingredient.of(Items.IRON_INGOT),                Rarity.UNCOMMON);
    public static final Stats GOLDEN  = new Stats(8.0, -2.9F, 8.0,  22, Ingredient.of(Items.GOLD_INGOT),                Rarity.UNCOMMON);
    public static final Stats DIAMOND = new Stats(9.0, -2.9F, 10.0, 10, Ingredient.of(Items.DIAMOND),                   Rarity.EPIC);
    public static final Stats NETHERITE = new Stats(10.0, -2.9F, 11.0, 15, Ingredient.of(Items.NETHERITE_INGOT),        Rarity.EPIC);
    public static final Stats STEEL   = new Stats(9.5, -2.8F, 10.5, 14, Ingredient.of(MutModItems.STEEL_INGOT),        Rarity.EPIC);
    public static final Stats GILDING = new Stats(10.0, -2.7F, 10.0, 22, Ingredient.of(MutModItems.GILDING_INGOT),      Rarity.EPIC);
    public static final Stats BLUE_DIAMOND = new Stats(11.0, -2.7F, 12.0, 10, Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT), Rarity.EPIC);
    public static final Stats ADVANCED_STEEL = new Stats(11.0, -2.8F, 12.0, 14, Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT), Rarity.EPIC);

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
    }

    public static Stats get(Item item) {
        return STATS_MAP.entrySet().stream()
                .filter(e -> e.getKey().get() == item)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(IRON);
    }
}