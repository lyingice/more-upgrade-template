package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.StrengthBlessingHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class StrengthBlessingMixin {

    @ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyMeleeDamage(float damage, DamageSource source) {
        if (!(source.getDirectEntity() instanceof Player player)) return damage;

        int level = StrengthBlessingHelper.getEquippedStrengthBlessingLevel(player);
        if (level <= 0) return damage;

        return damage * StrengthBlessingHelper.getMultiplier(player);
    }
}