package net.mcreator.mut.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class WitherDamageMixin {

    @ModifyVariable(
        method = "hurt",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private float modifyWitherDamage(float damage, DamageSource source) {
        // 凋零伤害类型是 WITHER
        if (!source.is(DamageTypes.WITHER)) {
            return damage;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        Holder<MobEffect> witherMark = BuiltInRegistries.MOB_EFFECT.getHolder(
            ResourceLocation.fromNamespaceAndPath("mut", "wither_mark")
        ).orElse(null);

        if (witherMark == null) return damage;

        MobEffectInstance mark = entity.getEffect(witherMark);
        if (mark != null) {
            int level = mark.getAmplifier() + 1;
            return damage + level;
        }

        return damage;
    }
}