package net.mcreator.mut.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.client.model.ModelCustomModel;
import net.mcreator.mut.client.model.ModelMutTridentModel;
import net.mcreator.mut.client.renderer.MutThrownTridentRenderer;
import net.mcreator.mut.client.renderer.layer.MutHorseArmorLayer;
import net.mcreator.mut.client.renderer.layer.MutWolfArmorLayer;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class MutClient {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelMutTridentModel.LAYER_LOCATION, ModelMutTridentModel::createBodyLayer);
        event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MutModItems.MUT_THROWN_TRIDENT.get(), MutThrownTridentRenderer::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        var wolfRenderer = event.getRenderer(EntityType.WOLF);
        if (wolfRenderer instanceof WolfRenderer wr) {
            wr.addLayer(new MutWolfArmorLayer(wr, event.getEntityModels()));
        }
        var horseRenderer = event.getRenderer(EntityType.HORSE);
        if (horseRenderer instanceof HorseRenderer hr) {
            hr.addLayer(new MutHorseArmorLayer(hr, event.getEntityModels()));
        }
    }

}