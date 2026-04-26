package net.mcreator.mut.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.mut.procedures.GetBeefHurtProcedure;

public class GoldenBeefItem extends Item {
	public GoldenBeefItem() {
		super(new Item.Properties().durability(16).food((new FoodProperties.Builder()).nutrition(7).saturationModifier(1.1f).alwaysEdible().build()));
	}

	@Override
	public int getEnchantmentValue() {
		return 2;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 29;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.GOLD_INGOT)).test(repairitem);
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