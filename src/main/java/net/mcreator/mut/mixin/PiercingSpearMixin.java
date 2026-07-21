package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.PiercingSpearHelper;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(net.minecraft.world.entity.LivingEntity.class)
@Deprecated
public class PiercingSpearMixin {

    @ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifySpearDamage(float damage, DamageSource source) {
        if (!(source.getDirectEntity() instanceof Player player)) return damage;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SpearItem)) return damage;

        int level = PiercingSpearHelper.getEquippedPiercingSpearLevel(player);
        if (level <= 0) return damage;

        boolean isCharging = player.getDeltaMovement().horizontalDistance() > 0.3F;
        float multiplier = isCharging
                ? PiercingSpearHelper.getChargeMultiplier(player)
                : PiercingSpearHelper.getStabMultiplier(player);

        return damage * multiplier;
    }
}
