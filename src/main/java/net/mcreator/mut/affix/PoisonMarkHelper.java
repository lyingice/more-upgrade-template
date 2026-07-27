package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.PoisonMarkAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class PoisonMarkHelper {
    private static final PoisonMarkAffix INSTANCE = new PoisonMarkAffix();
    @Deprecated public static int getTotalPoisonMarkLevel(LivingEntity target) { return INSTANCE.getTotalMarkLevel(target); }
    @Deprecated public static int getEquippedPoisonMarkLevel(LivingEntity entity) { return INSTANCE.getEquippedLevel(entity); }
    @Deprecated public static float getDamageBonus(LivingEntity target) { return INSTANCE.getDamageBonus(target); }
}
