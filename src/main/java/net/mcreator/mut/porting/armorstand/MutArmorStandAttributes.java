package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.MutMod;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** 展示架实体属性注册（移植自 TCR 的 TcrEntityAttributes）。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MutArmorStandAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                MutArmorStandEntities.HORSE_ARMOR_STAND.get(),
                HorseArmorStandEntity.createAttributes().build()
        );
        event.put(
                MutArmorStandEntities.WOLF_ARMOR_STAND.get(),
                WolfArmorStandEntity.createAttributes().build()
        );
    }

    private MutArmorStandAttributes() {
    }
}
