package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.event.AffixEventHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 替代 SharpshooterMixin - 仅保留箭头伤害加成逻辑，印记施加已由 AffixDamageMixin 统一处理 */
@Mixin(AbstractArrow.class)
public class AffixProjectileMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void onArrowHit(EntityHitResult result, CallbackInfo ci) {
        // 印记施加和伤害倍率已由 AffixDamageMixin.modifyAffixDamage 统一处理
        // 此 mixin 保留用于未来可能的箭头专用效果
    }
}