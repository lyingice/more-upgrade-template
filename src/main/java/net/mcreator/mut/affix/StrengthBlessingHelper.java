package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.StrengthBlessingAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class StrengthBlessingHelper {
    private static final StrengthBlessingAffix INSTANCE = new StrengthBlessingAffix();
    @Deprecated public static int getEquippedStrengthBlessingLevel(LivingEntity e) { return INSTANCE.getEquippedLevel(e); }
    @Deprecated public static float getMultiplier(LivingEntity e) { return INSTANCE.getMultiplier(e); }
}
