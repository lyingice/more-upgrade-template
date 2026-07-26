package net.mcreator.mut.trait.effect;

import net.mcreator.mut.trait.TraitEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class AttributeModifierSelf implements TraitEffect {
    private final String trigger;
    private final Holder<Attribute> attribute;
    private final AttributeModifier.Operation operation;
    private final float amount;
    private final int durationTicks;
    private final EntityFilter filter;

    public AttributeModifierSelf(String trigger, String attributeId, String op, float amount, int durationTicks, EntityFilter filter) {
        this.trigger = trigger;
        this.attribute = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(attributeId)).orElseThrow();
        this.operation = AttributeModifier.Operation.valueOf(op.toUpperCase());
        this.amount = amount;
        this.durationTicks = durationTicks;
        this.filter = filter;
    }

    @Override public String getTrigger() { return trigger; }
    @Override public void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState broken) {
        if (user != null && filter.matches(target != null ? target : user)) {
            var attr = user.getAttribute(attribute);
            if (attr != null) {
                var mod = new AttributeModifier(
                        ResourceLocation.parse("mut:trait_" + attribute.getRegisteredName()),
                        amount, operation);
                attr.addTransientModifier(mod);
            }
        }
    }
}