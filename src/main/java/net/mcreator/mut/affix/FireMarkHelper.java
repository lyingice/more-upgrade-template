package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.FireMarkAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class FireMarkHelper {

    public static final int MAX_LEVEL = 6;

    public static int getFireMarkLevel(LivingEntity entity) {
        int count = 0;

        if (hasFireMark(entity.getMainHandItem())) count++;
        if (hasFireMark(entity.getOffhandItem())) count++;
        if (hasFireMark(entity.getItemBySlot(EquipmentSlot.HEAD))) count++;
        if (hasFireMark(entity.getItemBySlot(EquipmentSlot.CHEST))) count++;
        if (hasFireMark(entity.getItemBySlot(EquipmentSlot.LEGS))) count++;
        if (hasFireMark(entity.getItemBySlot(EquipmentSlot.FEET))) count++;

        return Math.min(count, MAX_LEVEL);
    }

    private static boolean hasFireMark(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Affix affix = Affix.fromStack(stack);
        return affix instanceof FireMarkAffix;
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