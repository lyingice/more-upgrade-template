package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.BigStomachAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class BigStomachHelper {

    public static int getEquippedBigStomachLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.BIG_STOMACH.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof BigStomachAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    public static float getHealBonus(LivingEntity entity) {
        return getEquippedBigStomachLevel(entity) * (float) AffixConfig.getCoefficient("big_stomach");
    }
}