package net.mcreator.mut.mixin;

import net.mcreator.mut.potion.PoisonMarkMobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class PoisonDamageMixin {

    /**
     * 在 LivingEntity.hurt 方法被调用时，检查是否是中毒伤害
     * 如果是，且目标有剧毒印记，则增加伤害
     */
    @ModifyVariable(
        method = "hurt",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private float modifyPoisonDamage(float damage, DamageSource source) {
        // 安全检查：只处理 NeoForge 的中毒伤害
        if (!source.is(NeoForgeMod.POISON_DAMAGE)) {
            return damage;
        }

        // 获取当前实体（this 就是 LivingEntity）
        LivingEntity entity = (LivingEntity) (Object) this;

        // 检查是否有剧毒印记
        MobEffectInstance mark = entity.getEffect(
                Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getHolder(
                        ResourceLocation.fromNamespaceAndPath("mut", "poison_mark")
                ).orElse(null))
        );

        if (mark != null) {
            int markLevel = PoisonMarkMobEffect.getMarkLevel(mark);
            return damage + markLevel;
        }

        return damage;
    }
}