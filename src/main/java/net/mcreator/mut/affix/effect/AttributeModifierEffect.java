package net.mcreator.mut.affix.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;

public class AttributeModifierEffect implements AffixEffect {
    private final String trigger, slotName, attributeId, operation;
    private final double value;

    public AttributeModifierEffect(String trigger, String slotName, String attributeId, double value, String operation) {
        this.trigger = trigger; this.slotName = slotName; this.attributeId = attributeId;
        this.value = value; this.operation = operation;
    }
    @Override public String getTrigger() { return trigger; }
    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {}

    public ItemAttributeModifiers modify(ItemAttributeModifiers original, EquipmentSlot slot, String affixId) {
        Holder<Attribute> attr = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(attributeId)).orElse(null);
        if (attr == null) return original;
        EquipmentSlotGroup group = switch (slot) {
            case MAINHAND -> EquipmentSlotGroup.MAINHAND;
            case OFFHAND -> EquipmentSlotGroup.OFFHAND;
            case HEAD -> EquipmentSlotGroup.HEAD;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case LEGS -> EquipmentSlotGroup.LEGS;
            case FEET -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };
        AttributeModifier.Operation op = switch (this.operation) {
            case "multiply_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case "multiply_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> AttributeModifier.Operation.ADD_VALUE;
        };
        return original.withModifierAdded(attr,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", affixId + "." + attributeId.replace(":", ".")),
                        value, op), group);
    }
}