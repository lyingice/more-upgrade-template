package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.MomentumHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Deprecated
@Mixin(LivingEntity.class)
public class MomentumMixin {

    /*@ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifySmashAttackDamage(float damage, DamageSource source) {
        if (!(source.getDirectEntity() instanceof LivingEntity attacker)) return damage;
        if (attacker.onGround()) return damage;
        if (attacker.fallDistance <= 0.0F) return damage;

        float multiplier = MomentumHelper.getMultiplier(attacker);
        return damage * multiplier;
    }*/
}