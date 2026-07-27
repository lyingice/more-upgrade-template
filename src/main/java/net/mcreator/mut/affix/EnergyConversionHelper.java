package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.EnergyConversionAffix;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;

@Deprecated
public class EnergyConversionHelper {
    private static final EnergyConversionAffix INSTANCE = new EnergyConversionAffix();
    @Deprecated public static final int SATURATION_PER_DURABILITY = EnergyConversionAffix.SATURATION_PER_DURABILITY;
    @Deprecated public static int getEquippedEnergyConversionLevel(LivingEntity e) { return INSTANCE.getEquippedLevel(e); }
    @Deprecated public static List<EquipmentSlot> getEnergyConversionSlots(LivingEntity e) { return INSTANCE.getEnergyConversionSlots(e); }
    @Deprecated public static int getMaxRepair(LivingEntity e) { return INSTANCE.getMaxRepair(e); }
}
