package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.RegenerationMarkHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class RegenerationMixin {

    @ModifyVariable(
            method = "heal",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyHealAmount(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        float bonus = RegenerationMarkHelper.getHealBonus(entity);
        return amount + bonus;
    }
}