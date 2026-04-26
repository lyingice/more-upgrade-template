package net.mcreator.mut.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.mut.procedures.GetBeefHurtProcedure;

public class NetheriteBeefItem extends Item {
	public NetheriteBeefItem() {
		super(new Item.Properties().durability(96).fireResistant().food((new FoodProperties.Builder()).nutrition(9).saturationModifier(1.2f).alwaysEdible().build()));
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 23;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.NETHERITE_SCRAP)).test(repairitem);
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