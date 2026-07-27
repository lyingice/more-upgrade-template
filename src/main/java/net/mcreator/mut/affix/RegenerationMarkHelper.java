package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.RegenerationMarkAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class RegenerationMarkHelper {
    private static final RegenerationMarkAffix INSTANCE = new RegenerationMarkAffix();
    @Deprecated public static int getEquippedRegenerationMarkLevel(LivingEntity entity) { return INSTANCE.getEquippedLevel(entity); }
    @Deprecated public static float getHealBonus(LivingEntity entity) { return INSTANCE.getHealBonus(entity); }
}
