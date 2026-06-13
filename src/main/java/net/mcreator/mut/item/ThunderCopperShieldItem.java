package net.mcreator.mut.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutModAttributes;
import net.mcreator.mut.MutMod;

public class ThunderCopperShieldItem extends ShieldItem {
	public ThunderCopperShieldItem() {
		super(new Item.Properties().durability(1352)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "thunder_copper_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
						.add(MutModAttributes.THUNDER_POWER, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "thunder_copper_shield_1"), 20, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND).build())
				.rarity(Rarity.UNCOMMON).fireResistant());
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(MutModItems.THUNDER_COPPER_STAR.get())).test(repairitem);
	}
}