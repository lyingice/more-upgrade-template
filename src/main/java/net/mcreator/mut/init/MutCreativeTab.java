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
        }

        if (event.getTabKey() == combatTab) { //MUT战斗用品
            event.accept(MutModItems.IRON_CROSSBOW.get());
            event.accept(MutModItems.GOLDEN_CROSSBOW.get());
            event.accept(MutModItems.DIAMOND_CROSSBOW.get());
            event.accept(MutModItems.NETHERITE_CROSSBOW.get());
            event.accept(MutModItems.WOODEN_TRIDENT.get());
            event.accept(MutModItems.COPPER_TRIDENT.get());
            event.accept(MutModItems.IRON_TRIDENT.get());
            event.accept(MutModItems.GOLDEN_TRIDENT.get());
            event.accept(MutModItems.DIAMOND_TRIDENT.get());
            event.accept(MutModItems.NETHERITE_TRIDENT.get());
            event.accept(MutModItems.STEEL_TRIDENT.get());
            event.accept(MutModItems.GILDING_TRIDENT.get());
            event.accept(MutModItems.BLUE_DIAMOND_TRIDENT.get());
            event.accept(MutModItems.ADVANCED_STEEL_TRIDENT.get());
            //event.accept(MutModItems..get());
            event.insertAfter(new ItemStack(MutModItems.DRAGON_AXE.get()), MutModItems.AMETHYST_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_SWORD.get().getDefaultInstance(), MutModItems.AMETHYST_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_AXE.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_SWORD.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.insertAfter(new ItemStack(MutModItems.DRAGON_CHESTPLATE_ELYTRA.get()), MutModItems.AMETHYST_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_HELMET.get().getDefaultInstance(), MutModItems.AMETHYST_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_CHESTPLATE.get().getDefaultInstance(), MutModItems.AMETHYST_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_LEGGINGS.get().getDefaultInstance(), MutModItems.AMETHYST_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.AMETHYST_BOOTS.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_HELMET.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_CHESTPLATE.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(MutModItems.NETHERITE_AMETHYST_LEGGINGS.get().getDefaultInstance(), MutModItems.NETHERITE_AMETHYST_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}