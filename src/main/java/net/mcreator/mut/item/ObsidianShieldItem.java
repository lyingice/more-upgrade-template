package net.mcreator.mut.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.MutMod;

public class ObsidianShieldItem extends ShieldItem {
	public ObsidianShieldItem() {
		super(new Item.Properties().durability(1661)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "obsidian_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
						.add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "obsidian_shield_1"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "obsidian_shield_2"), 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND).build())
				.fireResistant());
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Blocks.OBSIDIAN)).test(repairitem);
	}
}