package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.PiercingSpearAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class PiercingSpearHelper {

    public static final float STAB_MULTIPLIER = 0.10F;
    public static final float CHARGE_MULTIPLIER = 0.05F;

    public static int getEquippedPiercingSpearLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.PIERCING_SPEAR.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof PiercingSpearAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    /** 戳击伤害倍率 */
    public static float getStabMultiplier(LivingEntity entity) {
        int level = getEquippedPiercingSpearLevel(entity);
        return 1.0F + level * (float) AffixConfig.getCoefficient("piercing_spear_stab");
    }

    /** 冲刺伤害倍率 */
    public static float getChargeMultiplier(LivingEntity entity) {
        int level = getEquippedPiercingSpearLevel(entity);
        return 1.0F + level * (float) AffixConfig.getCoefficient("piercing_spear_charge");
    }
}