/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mut.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.mut.client.renderer.*;

@EventBusSubscriber(Dist.CLIENT)
public class MutModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MutModEntities.ABORIGINE_ZOMBIE.get(), AborigineZombieRenderer::new);
		event.registerEntityRenderer(MutModEntities.RED_LIGHTNING_CREEPER.get(), RedLightningCreeperRenderer::new);
		event.registerEntityRenderer(MutModEntities.TRAVELER_PHANTOM.get(), TravelerPhantomRenderer::new);
		event.registerEntityRenderer(MutModEntities.LITTLE_CREEPER.get(), LittleCreeperRenderer::new);
		event.registerEntityRenderer(MutModEntities.ELITE_ABORIGINE_ZOMBIE.get(), EliteAborigineZombieRenderer::new);
		event.registerEntityRenderer(MutModEntities.PIGLIN_EXPEDITIONARY.get(), PiglinExpeditionaryRenderer::new);
		event.registerEntityRenderer(MutModEntities.FAT_PIGLIN_EXPEDITIONARY.get(), FatPiglinExpeditionaryRenderer::new);
		event.registerEntityRenderer(MutModEntities.STEEL_SLIME.get(), SteelSlimeRenderer::new);
	}
}