package net.mcreator.mut.affix.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MarkOnAttackEffect implements AffixEffect {
    private final String trigger;
    private final String effectId;
    private final Holder<MobEffect> effect;
    private final int durationTicks;

    public MarkOnAttackEffect(String trigger, String effectId, int durationTicks) {
        this.trigger = trigger;
        this.effectId = effectId;
        this.effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId)).orElse(null);
        this.durationTicks = durationTicks;
    }

    @Override public String getTrigger() { return trigger; }

    /** 用于去重分组：唯一标识一个印记效果 */
    public String getEffectKey() { return effectId; }

    public int getDurationTicks() { return durationTicks; }

    /** 从 effectKey 反解析 effectId */
    public static String parseEffectId(String key) { return key; }

    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {
        if (target == null || effect == null) return;
        MobEffectInstance existing = target.getEffect(effect);
        int newAmp = level - 1;
        if (existing != null) newAmp = Math.max(newAmp, existing.getAmplifier());
        target.addEffect(new MobEffectInstance(effect, durationTicks, newAmp, false, true, true));
    }
}