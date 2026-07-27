package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.MomentumAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class MomentumHelper {
    private static final MomentumAffix INSTANCE = new MomentumAffix();
    @Deprecated public static final float MULTIPLIER_PER_LEVEL = 0.125F;
    @Deprecated public static int getEquippedMomentumLevel(LivingEntity entity) { return INSTANCE.getEquippedLevel(entity); }
    @Deprecated public static float getMultiplier(LivingEntity entity) { return INSTANCE.getMultiplier(entity); }
}
