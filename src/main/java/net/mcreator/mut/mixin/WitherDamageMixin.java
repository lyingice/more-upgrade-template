package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.WitherMarkHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
        if (!source.is(DamageTypes.WITHER)) return damage;

        LivingEntity entity = (LivingEntity) (Object) this;
        float bonus = WitherMarkHelper.getDamageBonus(entity);
        return damage + bonus;
    }
}