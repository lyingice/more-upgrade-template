package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;

public class NirvanaAffix implements Affix {

    public static final String AFFIX_ID = "nirvana";
    public static final float DURABILITY_MULTIPLIER = 1.25F;

    @Override
    public String getId() {
        return AFFIX_ID;
    }

    //@Override
    public boolean hasAttributeModifiers() {
        return true;
    }

    //@Override
    public ItemAttributeModifiers getAttributeModifiers(ItemAttributeModifiers original, EquipmentSlot slot) {
        ItemAttributeModifiers modifiers = original;

        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            // 武器/工具：攻击+1、攻速+0.1、效率+1
            modifiers = modifiers.withModifierAdded(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", "nirvana.attack_damage"),
                            1.0,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    slot == EquipmentSlot.MAINHAND ? EquipmentSlotGroup.MAINHAND : EquipmentSlotGroup.OFFHAND
            );
            modifiers = modifiers.withModifierAdded(
                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", "nirvana.attack_speed"),
                            0.1,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    slot == EquipmentSlot.MAINHAND ? EquipmentSlotGroup.MAINHAND : EquipmentSlotGroup.OFFHAND
            );
            modifiers = modifiers.withModifierAdded(
                    Attributes.MINING_EFFICIENCY,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", "nirvana.mining_efficiency"),
                            1.0,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    slot == EquipmentSlot.MAINHAND ? EquipmentSlotGroup.MAINHAND : EquipmentSlotGroup.OFFHAND
            );
        } else if (slot.isArmor()) {
            // 盔甲：护甲+1、韧性+0.5、生命+2
            EquipmentSlotGroup group = switch (slot) {
                case HEAD -> EquipmentSlotGroup.HEAD;
                case CHEST -> EquipmentSlotGroup.CHEST;
                case LEGS -> EquipmentSlotGroup.LEGS;
                case FEET -> EquipmentSlotGroup.FEET;
                default -> EquipmentSlotGroup.ANY;
            };
            modifiers = modifiers.withModifierAdded(
                    Attributes.ARMOR,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", "nirvana.armor"),
                            1.0,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    group
            );
            modifiers = modifiers.withModifierAdded(
                    Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", "nirvana.armor_toughness"),
                            0.5,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    group
            );
            modifiers = modifiers.withModifierAdded(
                    Attributes.MAX_HEALTH,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", "nirvana.max_health"),
                            2.0,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    group
            );
        }

        return modifiers;
    }
}