package net.mcreator.mut.item;

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

public class DragonShieldItem extends ShieldItem {
	public DragonShieldItem() {
		super(new Item.Properties().durability(3280)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "dragon_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "dragon_shield_1"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
						.add(Attributes.OXYGEN_BONUS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "dragon_shield_2"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.HAND).build())
				.rarity(Rarity.EPIC).fireResistant());
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(Items.NETHER_STAR)).test(repairitem);
	}
}