package net.mcreator.mut.compat.spearcore;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.item.BaseSpearItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 仅在 spearcore 已加载时由 {@link SpearcoreIntegration} 触发。
 * 负责把 mut 长矛注册进 {@link MutModItems#REGISTRY}。
 */
public final class MutSpearItems {

    private MutSpearItems() {
    }

    public static void register() {
        //MutModItems.WOODEN_SPEAR = MutModItems.REGISTRY.register("wooden_spear", BaseSpearItem.WoodenSpearItem::new);
        //MutModItems.STONE_SPEAR = MutModItems.REGISTRY.register("stone_spear", BaseSpearItem.StoneSpearItem::new);
        //MutModItems.COPPER_SPEAR = MutModItems.REGISTRY.register("copper_spear", BaseSpearItem.CopperSpearItem::new);
        //MutModItems.IRON_SPEAR = MutModItems.REGISTRY.register("iron_spear", BaseSpearItem.IronSpearItem::new);
        //MutModItems.GOLDEN_SPEAR = MutModItems.REGISTRY.register("golden_spear", BaseSpearItem.GoldenSpearItem::new);
        //MutModItems.DIAMOND_SPEAR = MutModItems.REGISTRY.register("diamond_spear", BaseSpearItem.DiamondSpearItem::new);
        //MutModItems.NETHERITE_SPEAR = MutModItems.REGISTRY.register("netherite_spear", BaseSpearItem.NetheriteSpearItem::new);
        MutModItems.STEEL_SPEAR = MutModItems.REGISTRY.register("steel_spear", BaseSpearItem.SteelSpearItem::new);
        MutModItems.ADVANCED_STEEL_SPEAR = MutModItems.REGISTRY.register("advanced_steel_spear", BaseSpearItem.AdvancedSteelSpearItem::new);
        MutModItems.GILDING_SPEAR = MutModItems.REGISTRY.register("gilding_spear", BaseSpearItem.GildingSpearItem::new);
        MutModItems.BLUE_DIAMOND_SPEAR = MutModItems.REGISTRY.register("blue_diamond_spear", BaseSpearItem.BlueDiamondSpearItem::new);
        MutModItems.OBSIDIAN_SPEAR = MutModItems.REGISTRY.register("obsidian_spear", BaseSpearItem.ObsidianSpearItem::new);
        MutModItems.NETHERITE_OBSIDIAN_SPEAR = MutModItems.REGISTRY.register("netherite_obsidian_spear", BaseSpearItem.NetheriteObsidianSpearItem::new);
        MutModItems.CRYING_OBSIDIAN_SPEAR = MutModItems.REGISTRY.register("crying_obsidian_spear", BaseSpearItem.CryingObsidianSpearItem::new);
        MutModItems.EMERALD_SPEAR = MutModItems.REGISTRY.register("emerald_spear", BaseSpearItem.EmeraldSpearItem::new);
        MutModItems.NETHERITE_EMERALD_SPEAR = MutModItems.REGISTRY.register("netherite_emerald_spear", BaseSpearItem.NetheriteEmeraldSpearItem::new);
        MutModItems.NETHERITE_REDSTONE_SPEAR = MutModItems.REGISTRY.register("netherite_redstone_spear", BaseSpearItem.NetheriteRedstoneSpearItem::new);
        MutModItems.NETHER_STAR_SPEAR = MutModItems.REGISTRY.register("nether_star_spear", BaseSpearItem.NetherStarSpearItem::new);
        MutModItems.WITHER_SPEAR = MutModItems.REGISTRY.register("wither_spear", BaseSpearItem.WitherSpearItem::new);
        MutModItems.DRAGON_SPEAR = MutModItems.REGISTRY.register("dragon_spear", BaseSpearItem.DragonSpearItem::new);
        MutModItems.AMETHYST_SPEAR = MutModItems.REGISTRY.register("amethyst_spear", BaseSpearItem.AmethystSpearItem::new);
        MutModItems.NETHERITE_AMETHYST_SPEAR = MutModItems.REGISTRY.register("netherite_amethyst_spear", BaseSpearItem.NetheriteAmethystSpearItem::new);
        MutModItems.NETHERITE_COPPER_SPEAR = MutModItems.REGISTRY.register("netherite_copper_spear", BaseSpearItem.NetheriteCopperSpearItem::new);
        MutModItems.LAPIS_LAZULI_SPEAR = MutModItems.REGISTRY.register("lapis_lazuli_spear", BaseSpearItem.LapisLazuliSpearItem::new);
        MutModItems.NETHERITE_LAPIS_LAZULI_SPEAR = MutModItems.REGISTRY.register("netherite_lapis_lazuli_spear", BaseSpearItem.NetheriteLapisLazuliSpearItem::new);
        MutModItems.ECHOITE_SPEAR = MutModItems.REGISTRY.register("echoite_spear", BaseSpearItem.EchoiteSpearItem::new);
        MutModItems.POISON_STEEL_SPEAR = MutModItems.REGISTRY.register("poison_steel_spear", BaseSpearItem.PoisonSteelSpearItem::new);
        MutModItems.FLAME_GOLD_SPEAR = MutModItems.REGISTRY.register("flame_gold_spear", BaseSpearItem.FlameGoldSpearItem::new);
        MutModItems.THUNDER_COPPER_SPEAR = MutModItems.REGISTRY.register("thunder_copper_spear", BaseSpearItem.ThunderCopperSpearItem::new);
        MutModItems.UNCANNY_AMETHYST_SPEAR = MutModItems.REGISTRY.register("uncanny_amethyst_spear", BaseSpearItem.UncannyAmethystSpearItem::new);
    }
    private static void addSpearToTab(BuildCreativeModeTabContentsEvent event, ItemStack after, DeferredItem<Item> spear) {
        if (spear != null) {
            event.insertAfter(after, spear.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
