package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.MomentumAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class MomentumHelper {

    public static final float MULTIPLIER_PER_LEVEL = 0.125F;

    public static int getEquippedMomentumLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.MOMENTUM.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof MomentumAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    public static float getMultiplier(LivingEntity entity) {
        int level = getEquippedMomentumLevel(entity);
        return 1.0F + level * MULTIPLIER_PER_LEVEL;
    }
}