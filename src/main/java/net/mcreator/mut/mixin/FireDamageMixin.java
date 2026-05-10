package net.mcreator.mut.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class FireDamageMixin {

    @ModifyVariable(
        method = "hurt",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private float modifyFireDamage(float damage, DamageSource source) {
        // 检查是否是火焰伤害
        if (!source.is(DamageTypeTags.IS_FIRE)) {
            return damage;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        // 检查是否有灼烧印记
        Holder<MobEffect> fireMark = BuiltInRegistries.MOB_EFFECT.getHolder(
            ResourceLocation.fromNamespaceAndPath("mut", "fire_mark")
        ).orElse(null);

        if (fireMark == null) return damage;

        MobEffectInstance mark = entity.getEffect(fireMark);
        if (mark != null) {
            int level = mark.getAmplifier() + 1;
            return damage + level;
        }

        return damage;
    }
}