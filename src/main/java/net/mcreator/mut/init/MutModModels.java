/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mut.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.mut.client.model.*;

@EventBusSubscriber(Dist.CLIENT)
public class MutModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelFatPiglin.LAYER_LOCATION, ModelFatPiglin::createBodyLayer);
		event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
		event.registerLayerDefinition(Modelcreeper_charge.LAYER_LOCATION, Modelcreeper_charge::createBodyLayer);
		event.registerLayerDefinition(ModelMutTridentModel.LAYER_LOCATION, ModelMutTridentModel::createBodyLayer);
		event.registerLayerDefinition(Modelcreeper.LAYER_LOCATION, Modelcreeper::createBodyLayer);
		event.registerLayerDefinition(Modelphantom.LAYER_LOCATION, Modelphantom::createBodyLayer);
	}
}