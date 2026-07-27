package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.TidalSurgeAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public class TidalSurgeHelper {
    private static final TidalSurgeAffix INSTANCE = new TidalSurgeAffix();
    @Deprecated public static final int EXTRA_OXYGEN_PER_ITEM = TidalSurgeAffix.EXTRA_OXYGEN_PER_ITEM;
    @Deprecated public static final float WATER_SPEED_MULTIPLIER = TidalSurgeAffix.WATER_SPEED_MULTIPLIER;
    @Deprecated public static final float WATER_MINING_MULTIPLIER = TidalSurgeAffix.WATER_MINING_MULTIPLIER;
    @Deprecated public static int getEquippedTidalSurgeLevel(LivingEntity e) { return INSTANCE.getEquippedLevel(e); }
    @Deprecated public static int getExtraOxygen(LivingEntity e) { return INSTANCE.getExtraOxygen(e); }
    @Deprecated public static float getWaterSpeedMultiplier(LivingEntity e) { return INSTANCE.getWaterSpeedMultiplier(e); }
    @Deprecated public static float getWaterMiningMultiplier(LivingEntity e) { return INSTANCE.getWaterMiningMultiplier(e); }
    @Deprecated public static float getAttackMultiplier(LivingEntity e) { return INSTANCE.getAttackMultiplier(e); }
}
