package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.PoisonMarkHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class PoisonDamageMixin {

    @ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyPoisonDamage(float damage, DamageSource source) {
        if (!source.is(NeoForgeMod.POISON_DAMAGE)) return damage;

        LivingEntity entity = (LivingEntity) (Object) this;
        MobEffectInstance mark = entity.getEffect(
                BuiltInRegistries.MOB_EFFECT.getHolder(
                        ResourceLocation.fromNamespaceAndPath("mut", "poison_mark")
                ).orElse(null)
        );
        if (mark == null) return damage;

        int level = mark.getAmplifier() + 1;
        return damage + level * 0.5F;
    }
}