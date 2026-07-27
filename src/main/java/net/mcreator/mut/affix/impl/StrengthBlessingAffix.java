package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
@Deprecated
public class StrengthBlessingAffix implements Affix {

    public static final String AFFIX_ID = "strength_blessing";

    @Override public String getId() { return AFFIX_ID; }

    public float getMultiplier(LivingEntity entity) {
        return 1.0F + getEquippedLevel(entity) * (float) AffixConfig.getCoefficient("strength_blessing");
    }
}
