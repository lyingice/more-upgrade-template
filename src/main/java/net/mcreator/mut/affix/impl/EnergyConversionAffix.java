package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
@Deprecated
public class EnergyConversionAffix implements Affix {

    public static final String AFFIX_ID = "energy_conversion";
    public static final int SATURATION_PER_DURABILITY = 4;

    @Override public String getId() { return AFFIX_ID; }

    public List<EquipmentSlot> getEnergyConversionSlots(LivingEntity entity) {
        List<EquipmentSlot> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty() && Affix.fromStack(stack) instanceof EnergyConversionAffix) {
                slots.add(slot);
            }
        }
        return slots;
    }

    public int getMaxRepair(LivingEntity entity) {
        return Math.round(getEquippedLevel(entity) * (float) AffixConfig.getCoefficient("energy_conversion"));
    }
}
