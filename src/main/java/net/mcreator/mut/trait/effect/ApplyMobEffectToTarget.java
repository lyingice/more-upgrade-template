package net.mcreator.mut.trait.effect;

import net.mcreator.mut.trait.TraitEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ApplyMobEffectToTarget implements TraitEffect {
    private final String trigger;
    private final Holder<MobEffect> effect;
    private final ValueRange duration, amplifier;
    private final EntityFilter filter;

    public ApplyMobEffectToTarget(String trigger, String effectId, ValueRange duration, ValueRange amplifier, EntityFilter filter) {
        this.trigger = trigger;
        this.effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId)).orElseThrow();
        this.duration = duration;
        this.amplifier = amplifier;
        this.filter = filter;
    }

    @Override public String getTrigger() { return trigger; }
    @Override public void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState broken) {
        if (target != null && filter.matches(target)) {
            java.util.Random r = new java.util.Random();
            target.addEffect(new MobEffectInstance(effect, duration.rollInt(r), amplifier.rollInt(r)));
        }
    }
}