package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.EnergyConversionAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnergyConversionHelper {

    public static final int SATURATION_PER_DURABILITY = 4;

    public static int getEquippedEnergyConversionLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.ENERGY_CONVERSION.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof EnergyConversionAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    /**
     * 获取所有带能量转化词缀的装备槽位
     */
    public static List<EquipmentSlot> getEnergyConversionSlots(LivingEntity entity) {
        List<EquipmentSlot> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty() && Affix.fromStack(stack) instanceof EnergyConversionAffix) {
                slots.add(slot);
            }
        }
        return slots;
    }

    /**
     * 计算恢复上限（总等级 × 0.5）
     */
    public static int getMaxRepair(LivingEntity entity) {
        int level = getEquippedEnergyConversionLevel(entity);
        return Math.round(level * (float) AffixConfig.getCoefficient("energy_conversion"));
    }
}