package net.mcreator.mut.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class EmeraldShieldItem extends ShieldItem {
	public EmeraldShieldItem() {
		super(new Item.Properties().durability(780));
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.EMERALD)).test(repairitem);
	}
}