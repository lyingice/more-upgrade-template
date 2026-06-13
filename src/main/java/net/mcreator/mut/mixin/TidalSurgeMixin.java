package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.TidalSurgeHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(net.minecraft.world.entity.LivingEntity.class)
public class TidalSurgeMixin {

    @ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyTidalSurgeDamage(float damage, DamageSource source) {
        if (!(source.getDirectEntity() instanceof Player player)) return damage;

        return damage * TidalSurgeHelper.getAttackMultiplier(player);
    }
}