package net.mcreator.mut.item;

import net.mcreator.mut.api.IDurableFoodItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

public class NetherStarBeefItem extends Item implements IDurableFoodItem {
	public NetherStarBeefItem() {
		super(new Item.Properties().durability(256).fireResistant().rarity(Rarity.EPIC).food((new FoodProperties.Builder()).nutrition(20).saturationModifier(3f).alwaysEdible().build()));
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
		return 22;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 60;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.NETHER_STAR)).test(repairitem);
	}
}