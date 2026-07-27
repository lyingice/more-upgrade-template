package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.IMarkAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
@Deprecated
public class PoisonMarkAffix implements IMarkAffix {

    public static final String AFFIX_ID = "poison_mark";
    public static final int MARK_DURATION_TICKS = 600;

    @Override public String getId() { return AFFIX_ID; }
    @Override public int getMarkDurationTicks() { return MARK_DURATION_TICKS; }

    @Override
    public Holder<MobEffect> getMarkEffect() {
        return BuiltInRegistries.MOB_EFFECT.getHolder(
                ResourceLocation.fromNamespaceAndPath("mut", "poison_mark")).orElse(null);
    }

    @Override
    public int getMarkLevel(LivingEntity attacker) { return getEquippedLevel(attacker); }

    public int getTotalMarkLevel(LivingEntity target) {
        MobEffectInstance mark = target.getEffect(getMarkEffect());
        return mark == null ? 0 : mark.getAmplifier() + 1;
    }

    public float getDamageBonus(LivingEntity target) {
        return getTotalMarkLevel(target) * (float) AffixConfig.getCoefficient("poison_mark");
    }
}
