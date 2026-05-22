package net.mcreator.mut.init;

import net.mcreator.mut.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
public class MutCreativeTab {

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> toolsTab = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("mut", "more_upgrade_template_tools"));

        ResourceKey<CreativeModeTab> combatTab = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("mut", "more_upgrade_template_combat_items"));

        ResourceKey<CreativeModeTab> CombatTab = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("minecraft","combat"));

        if (event.getTabKey() == CombatTab) { //原版战斗物品
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.NETHERITE_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.DIAMOND_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.GOLDEN_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.IRON_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.COPPER_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.STONE_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.NETHERITE_SWORD),MutModItems.WOODEN_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(Items.IRON_HORSE_ARMOR),MutModItems.COPPER_HORSE_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.DIAMOND_HORSE_ARMOR),MutModItems.NETHERITE_HORSE_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.WOLF_ARMOR),MutModItems.NETHERITE_WOLF_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.WOLF_ARMOR),MutModItems.DIAMOND_WOLF_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.WOLF_ARMOR),MutModItems.GOLDEN_WOLF_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.WOLF_ARMOR),MutModItems.IRON_WOLF_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.WOLF_ARMOR),MutModItems.COPPER_WOLF_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == toolsTab) { //MUT工具
            event.accept(MutModItems.AMETHYST_SHOVEL.get());
            event.accept(MutModItems.AMETHYST_PICKAXE.get());
            event.accept(MutModItems.AMETHYST_AXE.get());
            event.accept(MutModItems.AMETHYST_HOE.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_SHOVEL.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_PICKAXE.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_AXE.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_HOE.get());
            event.accept(MutModItems.WITHER_SHOVEL.get());
            event.accept(MutModItems.WITHER_PICKAXE.get());
            event.accept(MutModItems.WITHER_AXE.get());
            event.accept(MutModItems.WITHER_HOE.get());
        }

        if (event.getTabKey() == combatTab) { //MUT战斗用品
            event.accept(MutModItems.DRAGON_BOW.get());
            event.accept(MutModItems.WITHER_BOW.get());
            event.accept(MutModItems.NETHERITE_REDSTONE_BOW.get());
            event.accept(MutModItems.EMERALD_BOW.get());
            event.accept(MutModItems.NETHERITE_EMERALD_BOW.get());
            event.accept(MutModItems.AMETHYST_BOW.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_BOW.get());
            event.accept(MutModItems.COPPER_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_COPPER_CROSSBOW.get());
            event.accept(MutModItems.IRON_CROSSBOW.get());
            event.accept(MutModItems.GOLDEN_CROSSBOW.get());
            event.accept(MutModItems.DIAMOND_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_CROSSBOW.get());
            event.accept(MutModItems.STEEL_CROSSBOW.get());
            event.accept(MutModItems.GILDING_CROSSBOW.get());
            event.accept(MutModItems.BLUE_DIAMOND_CROSSBOW.get());
            event.accept(MutModItems.ADVANCED_STEEL_CROSSBOW.get());
            event.accept(MutModItems.OBSIDIAN_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_OBSIDIAN_CROSSBOW.get());
            event.accept(MutModItems.CRYING_OBSIDIAN_CROSSBOW.get());
            event.accept(MutModItems.NETHER_STAR_CROSSBOW.get());
            event.accept(MutModItems.WITHER_CROSSBOW.get());
            event.accept(MutModItems.DRAGON_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_REDSTONE_CROSSBOW.get());
            event.accept(MutModItems.EMERALD_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_EMERALD_CROSSBOW.get());
            event.accept(MutModItems.AMETHYST_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_CROSSBOW.get());
            event.accept(MutModItems.WOODEN_TRIDENT.get());
            event.accept(MutModItems.COPPER_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_COPPER_TRIDENT.get());
            event.accept(MutModItems.IRON_TRIDENT.get());
            event.accept(MutModItems.GOLDEN_TRIDENT.get());
            event.accept(MutModItems.DIAMOND_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_TRIDENT.get());
            event.accept(MutModItems.STEEL_TRIDENT.get());
            event.accept(MutModItems.GILDING_TRIDENT.get());
            event.accept(MutModItems.BLUE_DIAMOND_TRIDENT.get());
            event.accept(MutModItems.ADVANCED_STEEL_TRIDENT.get());
            event.accept(MutModItems.OBSIDIAN_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_OBSIDIAN_TRIDENT.get());
            event.accept(MutModItems.CRYING_OBSIDIAN_TRIDENT.get());
            event.accept(MutModItems.NETHER_STAR_TRIDENT.get());
            event.accept(MutModItems.DRAGON_TRIDENT.get());
            event.accept(MutModItems.WITHER_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_REDSTONE_TRIDENT.get());
            event.accept(MutModItems.EMERALD_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_EMERALD_TRIDENT.get());
            event.accept(MutModItems.AMETHYST_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_AMETHYST_TRIDENT.get());
            //event.accept(MutModItems..get());
            event.insertAfter(MutModItems.DRAGON_AXE.get().getDefaultInstance(), MutModItems.AMETHYST_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.DRAGON_SWORD.get()), MutModItems.AMETHYST_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_SWORD.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_AXE.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_SWORD.get().getDefaultInstance(), MutModItems.WITHER_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_AXE.get().getDefaultInstance(), MutModItems.WITHER_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.COPPER_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.NETHERITE_COPPER_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.STEEL_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.ADVANCED_STEEL_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.GILDING_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.BLUE_DIAMOND_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.OBSIDIAN_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.NETHERITE_OBSIDIAN_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.CRYING_OBSIDIAN_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.EMERALD_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.NETHERITE_EMERALD_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.NETHERITE_REDSTONE_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.NETHER_STAR_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.WITHER_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.DRAGON_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.AMETHYST_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(MutModItems.COPPER_AXE.get()), MutModItems.NETHERITE_AMETHYST_SPEAR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.insertAfter(new ItemStack(MutModItems.ADVANCED_STEEL_MACE.get()), MutModItems.OBSIDIAN_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.OBSIDIAN_MACE.get()), MutModItems.NETHERITE_OBSIDIAN_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.NETHERITE_OBSIDIAN_MACE.get()), MutModItems.CRYING_OBSIDIAN_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.CRYING_OBSIDIAN_MACE.get()), MutModItems.NETHER_STAR_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.NETHER_STAR_MACE.get()), MutModItems.WITHER_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.WITHER_MACE.get()), MutModItems.DRAGON_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.DRAGON_MACE.get()), MutModItems.NETHERITE_COPPER_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.NETHERITE_COPPER_MACE.get()), MutModItems.NETHERITE_EMERALD_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.NETHERITE_EMERALD_MACE.get()), MutModItems.NETHERITE_REDSTONE_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.NETHERITE_REDSTONE_MACE.get()), MutModItems.NETHERITE_AMETHYST_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.NETHERITE_AMETHYST_MACE.get()), MutModItems.EMERALD_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(MutModItems.EMERALD_MACE.get()), MutModItems.AMETHYST_MACE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.insertAfter(new ItemStack(MutModItems.DRAGON_CHESTPLATE_ELYTRA.get()), MutModItems.AMETHYST_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_HELMET.get().getDefaultInstance(), MutModItems.AMETHYST_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_CHESTPLATE.get().getDefaultInstance(), MutModItems.AMETHYST_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_LEGGINGS.get().getDefaultInstance(), MutModItems.AMETHYST_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_BOOTS.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_HELMET.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_CHESTPLATE.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_LEGGINGS.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_BOOTS.get().getDefaultInstance(), MutModItems.WITHER_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.WITHER_HELMET.get().getDefaultInstance(), MutModItems.WITHER_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.WITHER_CHESTPLATE.get().getDefaultInstance(), MutModItems.WITHER_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.WITHER_LEGGINGS.get().getDefaultInstance(), MutModItems.WITHER_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.WITHER_BOOTS.get().getDefaultInstance(), MutModItems.SUPER_NETHERITE_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.SUPER_NETHERITE_HELMET.get().getDefaultInstance(), MutModItems.SUPER_NETHERITE_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.SUPER_NETHERITE_CHESTPLATE.get().getDefaultInstance(), MutModItems.SUPER_NETHERITE_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.SUPER_NETHERITE_LEGGINGS.get().getDefaultInstance(), MutModItems.SUPER_NETHERITE_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            //event.insertAfter(MutModItems.X.get().getDefaultInstance(), MutModItems.X.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}