package net.mcreator.mut.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
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
import net.mcreator.mut.MutMod;

public class PoisonSteelShieldItem extends ShieldItem {
	public PoisonSteelShieldItem() {
		super(new Item.Properties().durability(1352)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "poison_steel_shield_0"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND).build())
				.rarity(Rarity.UNCOMMON).fireResistant().component(DataComponents.CUSTOM_DATA, createAffixData()));
	}
    private static CustomData createAffixData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "poison_mark"
        );
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);}


    @Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(MutModItems.POSITION_STEEL_INGOT.get())).test(repairitem);
	}
}