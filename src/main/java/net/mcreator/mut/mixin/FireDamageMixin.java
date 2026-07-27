package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.FireMarkHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Deprecated
@Mixin(LivingEntity.class)
public class FireDamageMixin {

    /*@ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyFireDamage(float damage, DamageSource source) {
        if (!source.is(DamageTypeTags.IS_FIRE)) return damage;

        LivingEntity entity = (LivingEntity) (Object) this;
        float bonus = FireMarkHelper.getDamageBonus(entity);
        return damage + bonus;
    }*/
}