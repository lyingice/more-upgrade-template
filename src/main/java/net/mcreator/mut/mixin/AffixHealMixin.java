package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.event.AffixEventHandler;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** 替代 BigStomach/Regeneration 2 个 mixin */
@Mixin(LivingEntity.class)
public class AffixHealMixin {

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float modifyAffixHeal(float amount) {
        return AffixEventHandler.applyHealEffects((LivingEntity) (Object) this, amount);
    }
}