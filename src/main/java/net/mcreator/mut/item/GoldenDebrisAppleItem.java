package net.mcreator.mut.item;

import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.mut.procedures.DebrisAppleEatProcedure;

public class GoldenDebrisAppleItem extends BlockItem {
	public GoldenDebrisAppleItem() {
		super(MutModBlocks.GOLDEN_DEBRIS_APPLE.get(),new Item.Properties().fireResistant().food((new FoodProperties.Builder()).nutrition(6).saturationModifier(1f).alwaysEdible().build()));
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		DebrisAppleEatProcedure.execute(entity, itemstack);
		return retval;
	}
}