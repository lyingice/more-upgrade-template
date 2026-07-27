package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.event.AffixEventHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** 统一词缀伤害处理：替代 Fire/Momentum/Piercing/Poison/Strength/Tidal/Wither/Sharpshooter 8 个旧 mixin */
@Mixin(LivingEntity.class)
public class AffixDamageMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float modifyAffixDamage(float damage, DamageSource source) {
        LivingEntity target = (LivingEntity) (Object) this;

        // 1. 目标身上的印记增幅（毒/火/凋零 → 对应伤害类型额外伤害）
        damage += AffixEventHandler.applyMarkAmplify(target, source);

        // 2. 攻击者全身装备效果（伤害倍率 + 施加印记）
        // 攻击者全身装备效果
        LivingEntity attacker = resolveAttacker(source);
        boolean isProjectile = source.getDirectEntity() != source.getEntity()
                && !(source.getDirectEntity() instanceof LivingEntity);
        if (attacker != null) {
            damage = AffixEventHandler.applyDamageEffects(attacker, damage, source, isProjectile);
            AffixEventHandler.onAttack(attacker, target);
        }

        return damage;
    }

    /** 兼容近战/远程：近战用 getDirectEntity，远程（箭矢）用 getEntity（射手） */
    private static LivingEntity resolveAttacker(DamageSource source) {
        if (source.getDirectEntity() instanceof LivingEntity direct) return direct;
        if (source.getEntity() instanceof LivingEntity owner) return owner;
        return null;
    }
}