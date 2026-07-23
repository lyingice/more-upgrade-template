package net.mcreator.mut.client.renderer;

import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.PiglinModel;

import net.mcreator.mut.entity.PiglinExpeditionaryEntity;

public class PiglinExpeditionaryRenderer extends MobRenderer<PiglinExpeditionaryEntity, PiglinModel<PiglinExpeditionaryEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/piglin_expeditionary.png");

	public PiglinExpeditionaryRenderer(EntityRendererProvider.Context context) {
		super(context, new PiglinModel<PiglinExpeditionaryEntity>(context.bakeLayer(ModelLayers.PIGLIN)), 0.5f);
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	public ResourceLocation getTextureLocation(PiglinExpeditionaryEntity entity) {
		return entityTexture;
	}
}