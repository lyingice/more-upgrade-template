package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
@Deprecated
public class PiercingSpearAffix implements Affix {

    public static final String AFFIX_ID = "piercing_spear";

    @Override public String getId() { return AFFIX_ID; }

    public float getStabMultiplier(LivingEntity entity) {
        return 1.0F + getEquippedLevel(entity) * (float) AffixConfig.getCoefficient("piercing_spear_stab");
    }

    public float getChargeMultiplier(LivingEntity entity) {
        return 1.0F + getEquippedLevel(entity) * (float) AffixConfig.getCoefficient("piercing_spear_charge");
    }
}
