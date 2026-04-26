package net.mcreator.mut.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class NetherStarUpgradeTemplateItem extends Item {
	public NetherStarUpgradeTemplateItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.mut.nether_star_upgrade_template.description_0"));
		list.add(Component.translatable("item.mut.nether_star_upgrade_template.description_1"));
		list.add(Component.translatable("item.mut.nether_star_upgrade_template.description_2"));
		list.add(Component.translatable("item.mut.nether_star_upgrade_template.description_3"));
		list.add(Component.translatable("item.mut.nether_star_upgrade_template.description_4"));
		list.add(Component.translatable("item.mut.nether_star_upgrade_template.description_5"));
	}
}