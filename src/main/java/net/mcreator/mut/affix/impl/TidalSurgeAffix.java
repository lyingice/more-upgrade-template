package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
@Deprecated
public class TidalSurgeAffix implements Affix {

    public static final String AFFIX_ID = "tidal_surge";
    public static final int EXTRA_OXYGEN_PER_ITEM = 3;
    public static final float WATER_SPEED_MULTIPLIER = 0.25F;
    public static final float WATER_MINING_MULTIPLIER = 0.15F;

    @Override public String getId() { return AFFIX_ID; }

    public int getExtraOxygen(LivingEntity entity) {
        return getEquippedLevel(entity) * EXTRA_OXYGEN_PER_ITEM;
    }

    public float getWaterSpeedMultiplier(LivingEntity entity) {
        int level = getEquippedLevel(entity);
        return level <= 0 ? 1.0F : 1.0F + level * WATER_SPEED_MULTIPLIER;
    }

    public float getWaterMiningMultiplier(LivingEntity entity) {
        int level = getEquippedLevel(entity);
        return level <= 0 ? 1.0F : 1.0F + level * WATER_MINING_MULTIPLIER;
    }

    public float getAttackMultiplier(LivingEntity entity) {
        int level = getEquippedLevel(entity);
        if (level <= 0) return 1.0F;
        float bonus = 0F;
        if (entity.isInWater()) {
            bonus += level * (float) AffixConfig.getCoefficient("tidal_surge_water");
        } else if (entity.level().isRaining() && entity.level().canSeeSky(entity.blockPosition())) {
            bonus += level * (float) AffixConfig.getCoefficient("tidal_surge_rain");
        }
        return 1.0F + bonus;
    }
}
