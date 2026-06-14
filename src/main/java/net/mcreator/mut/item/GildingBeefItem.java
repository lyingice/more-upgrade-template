package net.mcreator.mut.item;

import net.mcreator.mut.api.IDurableFoodItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.world.level.LevelAccessor;

public class GildingBeefItem extends Item  implements IDurableFoodItem {
	public GildingBeefItem() {
		super(new Item.Properties().durability(64).fireResistant().food((new FoodProperties.Builder()).nutrition(8).saturationModifier(2f).alwaysEdible().build()));
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
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 16;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(MutModItems.GILDING_SCRAP.get())).test(repairitem);
	}
}