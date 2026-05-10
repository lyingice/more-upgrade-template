package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.WitherMarkAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class WitherMarkHelper {

    public static final int MAX_LEVEL = 6;

    public static int getWitherMarkLevel(LivingEntity entity) {
        int count = 0;
        if (hasWitherMark(entity.getMainHandItem())) count++;
        if (hasWitherMark(entity.getOffhandItem())) count++;
        if (hasWitherMark(entity.getItemBySlot(EquipmentSlot.HEAD))) count++;
        if (hasWitherMark(entity.getItemBySlot(EquipmentSlot.CHEST))) count++;
        if (hasWitherMark(entity.getItemBySlot(EquipmentSlot.LEGS))) count++;
        if (hasWitherMark(entity.getItemBySlot(EquipmentSlot.FEET))) count++;
        return Math.min(count, MAX_LEVEL);
    }

    private static boolean hasWitherMark(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Affix affix = Affix.fromStack(stack);
        return affix instanceof WitherMarkAffix;
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

    public static int getDamageBonus(int level) {
        return level;
    }
}