package net.mcreator.mut.client.renderer;

import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.mut.entity.FatPiglinExpeditionaryEntity;
import net.mcreator.mut.client.model.ModelFatPiglin;

public class FatPiglinExpeditionaryRenderer extends MobRenderer<FatPiglinExpeditionaryEntity, ModelFatPiglin<FatPiglinExpeditionaryEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/piglin_expeditionary.png");

	public FatPiglinExpeditionaryRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelFatPiglin<FatPiglinExpeditionaryEntity>(context.bakeLayer(ModelFatPiglin.LAYER_LOCATION)), 0.5f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	public ResourceLocation getTextureLocation(FatPiglinExpeditionaryEntity entity) {
		return entityTexture;
	}
}