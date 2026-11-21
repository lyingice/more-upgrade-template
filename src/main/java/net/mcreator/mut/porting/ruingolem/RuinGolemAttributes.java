package net.mcreator.mut.porting.ruingolem;

import net.mcreator.mut.MutMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** 傀儡属性注册（MOD 事件总线）。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class RuinGolemAttributes {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(RuinGolemEntities.OBSIDIAN_RUIN_GOLEM.get(), ObsidianRuinGolemEntity.createAttributes().build());
        event.put(RuinGolemEntities.CRYING_OBSIDIAN_RUIN_GOLEM.get(), CryingObsidianRuinGolemEntity.createAttributes().build());
        event.put(RuinGolemEntities.ROCK_RUIN_GOLEM.get(), RockRuinGolemEntity.createAttributes().build());
    }

    private RuinGolemAttributes() {
    }
}
