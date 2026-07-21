package net.mcreator.mut.init;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.spearcore.init.SpearStats;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * mut 长矛属性表。复用 spearcore 的 {@link SpearStats.Stats} 工厂，
 * 仅定义 mut 材质预设；运行时由 spearcore 的 {@link SpearStats#register} 管理。
 * <p>
 * 本类直接依赖 spearcore，只能在 spearcore 已加载时通过条件注册路径触达。
 */
public final class MutSpearStats {

    private MutSpearStats() {}

    private static final SoundEvent WOOD_USE = MutModSounds.ITEM_SPEAR_WOOD_USE.get();
    private static final SoundEvent WOOD_HIT = MutModSounds.ITEM_SPEAR_WOOD_HIT.get();
    private static final SoundEvent WOOD_ATTACK = MutModSounds.ITEM_SPEAR_WOOD_ATTACK.get();

    private static final SoundEvent SPEAR_USE = MutModSounds.ITEM_SPEAR_USE.get();
    private static final SoundEvent SPEAR_HIT = MutModSounds.ITEM_SPEAR_HIT.get();
    private static final SoundEvent SPEAR_ATTACK = MutModSounds.ITEM_SPEAR_ATTACK.get();

    public static final SpearStats.Stats WOOD = SpearStats.spearOf(59, 0.65f, 0.7f, 0.0f, 15, Rarity.COMMON, Ingredient.EMPTY, "wood", WOOD_USE, WOOD_HIT, WOOD_ATTACK, 0.75f, 5.0f, 10.0f, 15.0f);
    public static final SpearStats.Stats STONE = SpearStats.spearOf(131, 0.75f, 0.82f, 1.0f, 5, Rarity.COMMON, Ingredient.of(Items.COBBLESTONE), "stone", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.7f, 4.5f, 9.0f, 13.75f);
    public static final SpearStats.Stats COPPER = SpearStats.spearOf(195, 0.85f, 0.82f, 1.0f, 13, Rarity.COMMON, Ingredient.of(Items.COPPER_INGOT), "copper", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.65f, 4.0f, 8.25f, 12.5f);
    public static final SpearStats.Stats IRON = SpearStats.spearOf(250, 0.95f, 0.95f, 2.0f, 14, Rarity.COMMON, Ingredient.of(Items.IRON_INGOT), "iron", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.6f, 2.5f, 6.75f, 11.25f);
    public static final SpearStats.Stats GOLD = SpearStats.spearOf(59, 0.95f, 0.7f, 0.0f, 22, Rarity.COMMON, Ingredient.of(Items.GOLD_INGOT), "gold", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.7f, 3.5f, 8.5f, 13.75f);
    public static final SpearStats.Stats DIAMOND = SpearStats.spearOf(1561, 1.05f, 1.075f, 3.0f, 10, Rarity.COMMON, Ingredient.of(Items.DIAMOND), "diamond", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.5f, 3.0f, 6.5f, 10.0f);
    public static final SpearStats.Stats NETHERITE = SpearStats.spearOf(2031, 1.15f, 1.2f, 4.0f, 15, Rarity.COMMON, Ingredient.of(Items.NETHERITE_INGOT), true, "netherite", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats STEEL = SpearStats.spearOf(750, 0.87f, 1.125f, 3.5f, 15, Rarity.COMMON, Ingredient.of(MutModItems.STEEL_INGOT), true, "steel", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.6f, 2.5f, 6.75f, 11.25f);
    public static final SpearStats.Stats ADVANCED_STEEL = SpearStats.spearOf(2250, 0.87f, 1.3f, 5.0f, 15, Rarity.COMMON, Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT), true, "advanced_steel", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.6f, 2.5f, 6.75f, 11.25f);
    public static final SpearStats.Stats GILDING = SpearStats.spearOf(1561, 0.95f, 1.075f, 3.0f, 22, Rarity.COMMON, Ingredient.of(MutModItems.GILDING_INGOT), true, "gilding", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.7f, 3.5f, 8.5f, 13.75f);
    public static final SpearStats.Stats BLUE_DIAMOND = SpearStats.spearOf(2031, 0.87f, 1.3f, 5.0f, 18, Rarity.COMMON, Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT), true, "blue_diamond", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.5f, 3.0f, 6.5f, 10.0f);
    public static final SpearStats.Stats OBSIDIAN = SpearStats.spearOf(2650, 1.15f, 1.15f, 3.5f, 1, Rarity.COMMON, Ingredient.of(Items.OBSIDIAN), true, "obsidian", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats NETHERITE_OBSIDIAN = SpearStats.spearOf(3650, 1.30f, 1.3f, 5.0f, 1, Rarity.COMMON, Ingredient.of(MutModItems.OBSIDIAN_INGOT), true, "netherite_obsidian", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats CRYING_OBSIDIAN = SpearStats.spearOf(5650, 1.49f, 1.5f, 8.0f, 1, Rarity.EPIC, Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT), true, "crying_obsidian", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats DRAGON = SpearStats.spearOf(5888, 1.05f, 1.4f, 10.0f, 30, Rarity.EPIC, Ingredient.of(Items.NETHER_STAR), true, "dragon", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats NETHER_STAR = SpearStats.spearOf(3000, 1.15f, 1.4f, 7.0f, 22, Rarity.EPIC, Ingredient.of(Items.NETHER_STAR), true, "nether_star", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats WITHER = SpearStats.spearOf(3000, 1.15f, 1.4f, 7.0f, 22, Rarity.EPIC, Ingredient.of(Items.NETHER_STAR), true, "wither", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats NETHERITE_COPPER = SpearStats.spearOf(1231, 1.15f, 1.02f, 3.0f, 13, Rarity.COMMON, Ingredient.of(Items.COPPER_BLOCK), true, "netherite_copper", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats NETHERITE_REDSTONE = SpearStats.spearOf(2031, 0.93f, 1.2f, 4.0f, 15, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_REDSTONE_INGOT), true, "netherite_redstone", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats EMERALD = SpearStats.spearOf(888, 1.11f, 1.05f, 3.0f, 20, Rarity.COMMON, Ingredient.of(Items.EMERALD), "emerald", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.5f, 3.0f, 6.5f, 10.0f);
    public static final SpearStats.Stats NETHERITE_EMERALD = SpearStats.spearOf(1888, 1.15f, 1.175f, 5.0f, 20, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_EMERALD_INGOT), true, "netherite_emerald", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats AMETHYST = SpearStats.spearOf(350, 1.0f, 1.0f, 2.5f, 16, Rarity.COMMON, Ingredient.of(Items.AMETHYST_SHARD), "amethyst", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.6f, 2.5f, 6.75f, 11.25f);
    public static final SpearStats.Stats NETHERITE_AMETHYST = SpearStats.spearOf(1400, 1.15f, 1.15f, 4.5f, 16, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT), true, "netherite_amethyst", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats LAPIS_LAZULI = SpearStats.spearOf(200, 0.85F, 0.82F, 2.5F, 30, Rarity.COMMON, Ingredient.of(Items.LAPIS_LAZULI), "lapis_lazuli", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.7f, 4.5f, 9.0f, 13.75f);
    public static final SpearStats.Stats NETHERITE_LAPIS_LAZULI = SpearStats.spearOf(1000, 1.15F, 1.02F, 4.5F, 60, Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_LAPIS_LAZULI_INGOT), "netherite_lapis_lazuli", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats POISON_STEEL = SpearStats.spearOf(2031, 0.96F, 1.25F, 5F, 15, Rarity.UNCOMMON, Ingredient.of(MutModItems.POSITION_STEEL_INGOT), "position_steel", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.6f, 2.5f, 6.75f, 11.25f);
    public static final SpearStats.Stats FLAME_GOLD = SpearStats.spearOf(2031, 0.96F, 1.25F, 5F, 25, Rarity.UNCOMMON, Ingredient.of(MutModItems.FLAME_GOLD_INGOT), "flame_gold", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.7f, 3.5f, 8.5f, 13.75f);
    public static final SpearStats.Stats ECHOITE = SpearStats.spearOf(4062, 1.15F, 1.325F, 7F, 30, Rarity.EPIC, Ingredient.of(MutModItems.ECHOITE_INGOT), "echoite", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats THUNDER_COPPER = SpearStats.spearOf(2031, 0.85F, 1.25F, 6F, 1, Rarity.UNCOMMON, Ingredient.of(MutModItems.THUNDER_COPPER_STAR), "thunder_copper", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.65f, 4.0f, 8.25f, 12.5f);
    public static final SpearStats.Stats UNCANNY_AMETHYST = SpearStats.spearOf(2800, 1.15F, 1.25F, 7.5F, 16, Rarity.UNCOMMON, Ingredient.of(MutModItems.UNCANNY_AMETHYST_STAR), "uncanny_amethyst", SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK, 0.4f, 2.5f, 5.5f, 8.75f);
    public static final SpearStats.Stats DEFAULT = IRON;
}