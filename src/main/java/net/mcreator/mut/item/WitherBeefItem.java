package net.mcreator.mut.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.mut.procedures.GetBeefHurtProcedure;

public class WitherBeefItem extends Item {
	public WitherBeefItem() {
		super(new Item.Properties().durability(256).fireResistant().component(DataComponents.CUSTOM_DATA, createWitherMarkData()).rarity(Rarity.EPIC).food((new FoodProperties.Builder()).nutrition(10).saturationModifier(1.5f).alwaysEdible().build()));
	}

	@Override
	public int getEnchantmentValue() {
		return 22;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 20;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.NETHER_STAR)).test(repairitem);
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
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "wither_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }
}