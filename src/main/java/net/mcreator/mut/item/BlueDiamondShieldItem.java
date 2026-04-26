package net.mcreator.mut.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.MutMod;

public class BlueDiamondShieldItem extends ShieldItem {
	public BlueDiamondShieldItem() {
		super(new Item.Properties().durability(1117)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "blue_diamond_shield_0"), 2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "blue_diamond_shield_1"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND).build())
				.fireResistant());
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(MutModItems.BLUE_DIAMOND_INGOT.get())).test(repairitem);
	}
}