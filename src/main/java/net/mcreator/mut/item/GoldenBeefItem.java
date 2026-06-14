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

public class GoldenBeefItem extends Item  implements IDurableFoodItem {
	public GoldenBeefItem() {
		super(new Item.Properties().durability(16).food((new FoodProperties.Builder()).nutrition(7).saturationModifier(1.1f).alwaysEdible().build()));
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
}