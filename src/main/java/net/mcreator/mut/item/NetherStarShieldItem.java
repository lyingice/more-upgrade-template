package net.mcreator.mut.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.MutMod;

public class NetherStarShieldItem extends ShieldItem {
	public NetherStarShieldItem() {
		super(new Item.Properties().durability(1836)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "nether_star_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "nether_star_shield_1"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND).build())
				.rarity(Rarity.EPIC).fireResistant());
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.NETHER_STAR)).test(repairitem);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}
}