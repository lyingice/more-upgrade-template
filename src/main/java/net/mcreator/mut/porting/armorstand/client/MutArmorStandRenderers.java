package net.mcreator.mut.porting.armorstand.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.armorstand.MutArmorStandEntities;
import net.mcreator.mut.porting.armorstand.client.model.HorseArmorStandBaseModel;
import net.mcreator.mut.porting.armorstand.client.model.WolfArmorStandBaseModel;
import net.mcreator.mut.porting.armorstand.client.renderer.HorseArmorStandRenderer;
import net.mcreator.mut.porting.armorstand.client.renderer.WolfArmorStandRenderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** 展示架模型层与渲染器注册（移植自 TCR 的 TcrEntityRenderers）。 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class MutArmorStandRenderers {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                HorseArmorStandBaseModel.LAYER_LOCATION,
                HorseArmorStandBaseModel::createBodyLayer
        );
        event.registerLayerDefinition(
                WolfArmorStandBaseModel.LAYER_LOCATION,
                WolfArmorStandBaseModel::createBodyLayer
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                MutArmorStandEntities.HORSE_ARMOR_STAND.get(),
                HorseArmorStandRenderer::new
        );
        event.registerEntityRenderer(
                MutArmorStandEntities.WOLF_ARMOR_STAND.get(),
                WolfArmorStandRenderer::new
        );
    }

    private MutArmorStandRenderers() {
    }
}
