package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.MomentumAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class MomentumHelper {

    public static final int MAX_LEVEL = 6;

    public static int getMomentumLevel(LivingEntity entity) {
        int count = 0;
        if (hasMomentum(entity.getMainHandItem())) count++;
        if (hasMomentum(entity.getOffhandItem())) count++;
        if (hasMomentum(entity.getItemBySlot(EquipmentSlot.HEAD))) count++;
        if (hasMomentum(entity.getItemBySlot(EquipmentSlot.CHEST))) count++;
        if (hasMomentum(entity.getItemBySlot(EquipmentSlot.LEGS))) count++;
        if (hasMomentum(entity.getItemBySlot(EquipmentSlot.FEET))) count++;
        return Math.min(count, MAX_LEVEL);
    }

    private static boolean hasMomentum(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Affix affix = Affix.fromStack(stack);
        return affix instanceof MomentumAffix;
    }

    public static float getDamageMultiplier(int level) {
        return 1.0F + (level * MomentumAffix.DAMAGE_PER_LEVEL);
    }

    public static String getLevelName(int level) {
        return switch (level) {
            case 1 -> "Ⅰ";
            case 2 -> "Ⅱ";
            case 3 -> "Ⅲ";
            case 4 -> "Ⅳ";
            case 5 -> "Ⅴ";
            case 6 -> "Ⅵ";
            default -> "";
        };
    }
}