package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.WitherMarkAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class WitherMarkHelper {
    private static final WitherMarkAffix INSTANCE = new WitherMarkAffix();
    @Deprecated public static int getTotalWitherMarkLevel(LivingEntity target) { return INSTANCE.getTotalMarkLevel(target); }
    @Deprecated public static int getEquippedWitherMarkLevel(LivingEntity entity) { return INSTANCE.getEquippedLevel(entity); }
    @Deprecated public static float getDamageBonus(LivingEntity target) { return INSTANCE.getDamageBonus(target); }
}
