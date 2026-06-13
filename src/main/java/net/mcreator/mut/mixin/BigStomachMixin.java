package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.BigStomachHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class BigStomachMixin {

    @ModifyVariable(
            method = "heal",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyNaturalHeal(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Player player)) return amount;

        int level = BigStomachHelper.getEquippedBigStomachLevel(player);
        if (level <= 0) return amount;

        var foodData = player.getFoodData();
        if (foodData.getFoodLevel() >= 18 && foodData.getSaturationLevel() > 0) {
            return amount + level * 0.5F;
        }

        return amount;
    }
}