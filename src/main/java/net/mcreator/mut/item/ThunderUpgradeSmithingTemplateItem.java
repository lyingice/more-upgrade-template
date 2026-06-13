package net.mcreator.mut.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ThunderUpgradeSmithingTemplateItem extends Item {
	public ThunderUpgradeSmithingTemplateItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.mut.thunder_upgrade_smithing_template.description_0"));
		list.add(Component.translatable("item.mut.thunder_upgrade_smithing_template.description_1"));
		list.add(Component.translatable("item.mut.thunder_upgrade_smithing_template.description_2"));
		list.add(Component.translatable("item.mut.thunder_upgrade_smithing_template.description_3"));
		list.add(Component.translatable("item.mut.thunder_upgrade_smithing_template.description_4"));
		list.add(Component.translatable("item.mut.thunder_upgrade_smithing_template.description_5"));
	}
}