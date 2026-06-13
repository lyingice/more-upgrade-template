package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.IMarkAffix;
import net.mcreator.mut.affix.PoisonMarkHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class PoisonMarkAffix implements IMarkAffix {

    public static final String AFFIX_ID = "poison_mark";
    public static final int MARK_DURATION_TICKS = 600;

    @Override
    public String getId() { return AFFIX_ID; }

    @Override
    public int getMarkDurationTicks() { return MARK_DURATION_TICKS; }

    @Override
    public Holder<MobEffect> getMarkEffect() {
        return BuiltInRegistries.MOB_EFFECT.getHolder(
                ResourceLocation.fromNamespaceAndPath("mut", "poison_mark")
        ).orElse(null);
    }

    @Override
    public int getMarkLevel(LivingEntity attacker) {
        return PoisonMarkHelper.getEquippedPoisonMarkLevel(attacker);
    }
}