package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.FireMarkAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class FireMarkHelper {
    private static final FireMarkAffix INSTANCE = new FireMarkAffix();
    @Deprecated public static int getTotalFireMarkLevel(LivingEntity target) { return INSTANCE.getTotalMarkLevel(target); }
    @Deprecated public static int getEquippedFireMarkLevel(LivingEntity entity) { return INSTANCE.getEquippedLevel(entity); }
    @Deprecated public static float getDamageBonus(LivingEntity target) { return INSTANCE.getDamageBonus(target); }
}
