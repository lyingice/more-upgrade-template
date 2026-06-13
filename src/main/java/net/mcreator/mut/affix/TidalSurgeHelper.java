package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.TidalSurgeAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class TidalSurgeHelper {

    public static final int EXTRA_OXYGEN_PER_ITEM = 3;
    public static final float WATER_SPEED_MULTIPLIER = 0.25F;
    public static final float WATER_MINING_MULTIPLIER = 0.15F;
    public static final float WATER_ATTACK_MULTIPLIER = 0.10F;
    public static final float RAIN_ATTACK_MULTIPLIER = 0.05F;

    public static int getEquippedTidalSurgeLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.TIDAL_SURGE.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof TidalSurgeAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    public static int getExtraOxygen(LivingEntity entity) {
        return getEquippedTidalSurgeLevel(entity) * EXTRA_OXYGEN_PER_ITEM;
    }

    public static float getWaterSpeedMultiplier(LivingEntity entity) {
        int level = getEquippedTidalSurgeLevel(entity);
        if (level <= 0) return 1.0F;
        return 1.0F + level * WATER_SPEED_MULTIPLIER;
    }

    public static float getWaterMiningMultiplier(LivingEntity entity) {
        int level = getEquippedTidalSurgeLevel(entity);
        if (level <= 0) return 1.0F;
        return 1.0F + level * WATER_MINING_MULTIPLIER;
    }

    public static float getAttackMultiplier(LivingEntity entity) {
        int level = getEquippedTidalSurgeLevel(entity);
        if (level <= 0) return 1.0F;

        float bonus = 0F;
        if (entity.isInWater()) {
            bonus += level * WATER_ATTACK_MULTIPLIER;
        } else if (entity.level().isRaining() && entity.level().canSeeSky(entity.blockPosition())) {
            bonus += level * RAIN_ATTACK_MULTIPLIER;
        }
        return 1.0F + bonus;
    }
}