package net.mcreator.mut.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.MutMod;

public class UncannyAmethystShieldItem extends ShieldItem {
	public UncannyAmethystShieldItem() {
		super(new Item.Properties().durability(1736)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "uncanny_amethyst_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND).build())
				.rarity(Rarity.UNCOMMON).fireResistant().component(DataComponents.CUSTOM_DATA, createAffixData()));
	}private static CustomData createAffixData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "regeneration_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);}
}