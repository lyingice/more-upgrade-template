package net.mcreator.mut.item;

import net.mcreator.mut.api.IDurableFoodItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class DiamondBeefItem extends Item implements IDurableFoodItem {
	public DiamondBeefItem() {
		super(new Item.Properties().durability(64).food((new FoodProperties.Builder()).nutrition(8).saturationModifier(1.2f).alwaysEdible().build()));
	}
    private ItemStack currentStack;
    private Entity currentEntity;
    private LevelAccessor currentWorld;
    @Override
    public ItemStack getItemStack() {
        return currentStack;
    }

    @Override
    public Entity getEntity() {
        return currentEntity;
    }

    @Override
    public LevelAccessor getWorld() {
        return currentWorld;
    }

    @Override
    public void setContext(ItemStack stack, Entity entity, LevelAccessor world) {
        this.currentStack = stack;
        this.currentEntity = entity;
        this.currentWorld = world;
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
}