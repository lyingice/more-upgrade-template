package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.SharpshooterHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractArrow.class)
public class SharpshooterMixin {

    @ModifyVariable(
            method = "setBaseDamage",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private double modifyArrowDamage(double damage) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (!(arrow.getOwner() instanceof LivingEntity shooter)) return damage;

        float multiplier = SharpshooterHelper.getMultiplier(shooter);
        return damage * multiplier;
    }
}