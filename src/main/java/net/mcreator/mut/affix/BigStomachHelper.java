package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.BigStomachAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class BigStomachHelper {
    private static final BigStomachAffix INSTANCE = new BigStomachAffix();
    @Deprecated public static int getEquippedBigStomachLevel(LivingEntity e) { return INSTANCE.getEquippedLevel(e); }
    @Deprecated public static float getHealBonus(LivingEntity e) { return INSTANCE.getHealBonus(e); }
}
