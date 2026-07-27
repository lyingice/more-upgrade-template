package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

public class RegenerationMarkAffix implements Affix {

    public static final String AFFIX_ID = "regeneration_mark";

    @Override public String getId() { return AFFIX_ID; }

    public float getHealBonus(LivingEntity entity) {
        return getEquippedLevel(entity) * (float) AffixConfig.getCoefficient("regeneration_mark");
    }
}
