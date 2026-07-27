package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.PiercingSpearAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class PiercingSpearHelper {
    private static final PiercingSpearAffix INSTANCE = new PiercingSpearAffix();
    @Deprecated public static int getEquippedPiercingSpearLevel(LivingEntity entity) { return INSTANCE.getEquippedLevel(entity); }
    @Deprecated public static float getStabMultiplier(LivingEntity entity) { return INSTANCE.getStabMultiplier(entity); }
    @Deprecated public static float getChargeMultiplier(LivingEntity entity) { return INSTANCE.getChargeMultiplier(entity); }
}
