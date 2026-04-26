package net.mcreator.mut.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.mut.procedures.GetBeefHurtProcedure;

public class DiamondBeefItem extends Item {
	public DiamondBeefItem() {
		super(new Item.Properties().durability(64).food((new FoodProperties.Builder()).nutrition(8).saturationModifier(1.2f).alwaysEdible().build()));
	}

	@Override
	public int getEnchantmentValue() {
		return 3;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 26;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.DIAMOND)).test(repairitem);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		GetBeefHurtProcedure.execute(world, entity, itemstack);
		return retval;
	}
}