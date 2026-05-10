package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.MomentumHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class MomentumMixin {

    @ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifySmashAttackDamage(float damage, DamageSource source) {
        // 检查攻击者是否是 LivingEntity
        if (!(source.getDirectEntity() instanceof LivingEntity attacker)) {
            return damage;
        }

        // 检查攻击者是否在空中且正在下落（下落攻击的关键判定）
        if (attacker.onGround()) {
            return damage;
        }

        // 检查攻击者的下落距离（重锤需要下落才能触发额外伤害）
        if (attacker.fallDistance <= 0.0F) {
            return damage;
        }

        // 计算势能印记等级
        int level = MomentumHelper.getMomentumLevel(attacker);
        if (level <= 0) return damage;

        // 伤害乘以加成倍率
        float multiplier = MomentumHelper.getDamageMultiplier(level);
        return damage * multiplier;
    }
}