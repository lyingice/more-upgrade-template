package net.mcreator.mut.porting.mercenary.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.mercenary.MercenaryEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** 猪灵雇佣兵渲染器注册。 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class MercenaryRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MercenaryEntities.PIGLIN_MERCENARY.get(), PiglinMercenaryRenderer::new);
    }

    private MercenaryRenderers() {
    }
}
