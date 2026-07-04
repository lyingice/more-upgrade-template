package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.RegenerationMarkAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class RegenerationMarkHelper {

    public static int getEquippedRegenerationMarkLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.REGENERATION_MARK.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof RegenerationMarkAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    public static float getHealBonus(LivingEntity entity) {
        return getEquippedRegenerationMarkLevel(entity) * (float) AffixConfig.getCoefficient("regeneration_mark");
    }
}