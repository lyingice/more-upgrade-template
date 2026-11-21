package net.mcreator.mut.porting.mercenary;

import net.mcreator.mut.MutMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** 猪灵雇佣兵属性注册（MOD 事件总线）。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MercenaryAttributes {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(MercenaryEntities.PIGLIN_MERCENARY.get(), PiglinMercenaryEntity.createAttributes().build());
    }

    private MercenaryAttributes() {
    }
}
