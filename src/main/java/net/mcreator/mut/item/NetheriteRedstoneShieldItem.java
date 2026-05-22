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

public class NetheriteRedstoneShieldItem extends ShieldItem {
	public NetheriteRedstoneShieldItem() {
		super(new Item.Properties().durability(1352)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "netherite_redstone_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
						.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "netherite_redstone_shield_1"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.HAND)
						.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "netherite_redstone_shield_2"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.HAND).build())
				.fireResistant());
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(MutModItems.NETHERITE_REDSTONE_INGOT.get())).test(repairitem);
	}
}