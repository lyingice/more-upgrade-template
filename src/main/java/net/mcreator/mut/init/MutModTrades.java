/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.mut.init;

import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber
public class MutModTrades {
	@SubscribeEvent
	public static void registerTrades(VillagerTradesEvent event) {
		if (event.getType() == MutModVillagerProfessions.TEMPLATE_VENDOR.get()) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.EMERALD, 5), new ItemStack(MutModItems.STONE_UPGRADE_TEMPLATE.get()), 5, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.EMERALD, 8), new ItemStack(MutModItems.IRON_UPGRADE_TEMPLATE.get()), 5, 5, 0.05f));
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(Items.EMERALD, 14), new ItemStack(MutModItems.IRON_ARMOR_REBUILD_TEMPLATE.get()), 10, 15, 0.05f));
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(Items.EMERALD, 24), new ItemStack(MutModItems.DIAMOND_ARMOR_REBUILD_TEMPLATE.get()), 10, 15, 0.05f));
			event.getTrades().get(3).add(new BasicItemListing(new ItemStack(Items.EMERALD, 56), new ItemStack(Items.EMERALD, 44), new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 8, 20, 0.05f));
			event.getTrades().get(3).add(new BasicItemListing(new ItemStack(Items.EMERALD, 64), new ItemStack(MutModItems.STEEL_UPGRADE_TEMPLATE.get()), 4, 20, 0.05f));
			event.getTrades().get(3).add(new BasicItemListing(new ItemStack(Items.EMERALD, 64), new ItemStack(Items.EMERALD, 24), new ItemStack(MutModItems.GILDING_UPGRADE_TEMPLATE.get()), 4, 20, 0.05f));
			event.getTrades().get(4).add(new BasicItemListing(new ItemStack(Items.EMERALD, 64), new ItemStack(Items.EMERALD, 64), new ItemStack(MutModItems.BLUE_DIAMOND_UPGRADE_TEMPLATE.get()), 10, 20, 0.05f));
			event.getTrades().get(4).add(new BasicItemListing(new ItemStack(Items.EMERALD, 40), new ItemStack(Items.EMERALD, 10), new ItemStack(MutModItems.OBSIDIAN_UPGRADE_TEMPLATE.get()), 10, 20, 0.05f));
			event.getTrades().get(4).add(new BasicItemListing(new ItemStack(Items.EMERALD, 40), new ItemStack(Items.EMERALD, 54), new ItemStack(MutModItems.NETHERITE_OBSIDIAN_UPGRADE_TEMPLATE.get()), 10, 20, 0.05f));
			event.getTrades().get(4).add(new BasicItemListing(new ItemStack(Items.EMERALD, 64), new ItemStack(Blocks.EMERALD_BLOCK, 32), new ItemStack(MutModItems.NETHER_STAR_UPGRADE_TEMPLATE.get()), 10, 20, 0.05f));
			event.getTrades().get(5).add(new BasicItemListing(new ItemStack(Items.EMERALD, 32), new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 10, 20, 0.05f));
			event.getTrades().get(5).add(new BasicItemListing(new ItemStack(Blocks.EMERALD_BLOCK, 64), new ItemStack(MutModItems.CRYING_OBSIDIAN_UPGRADE_TEMPLATE.get()), 10, 20, 0.05f));
			event.getTrades().get(5).add(new BasicItemListing(new ItemStack(Blocks.EMERALD_BLOCK, 64), new ItemStack(MutModItems.DRAGON_UPGRADE_TEMPLATE.get()), 10, 20, 0.05f));
		}
	}
}