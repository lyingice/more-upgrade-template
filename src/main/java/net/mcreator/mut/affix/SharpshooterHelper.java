package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.SharpshooterAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class SharpshooterHelper {
    private static final SharpshooterAffix INSTANCE = new SharpshooterAffix();
    @Deprecated public static int getEquippedSharpshooterLevel(LivingEntity e) { return INSTANCE.getEquippedLevel(e); }
    @Deprecated public static float getMultiplier(LivingEntity e) { return INSTANCE.getMultiplier(e); }
}
