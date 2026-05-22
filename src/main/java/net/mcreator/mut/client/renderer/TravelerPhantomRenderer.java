package net.mcreator.mut.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.mut.entity.TravelerPhantomEntity;
import net.mcreator.mut.client.model.Modelphantom;

public class TravelerPhantomRenderer extends MobRenderer<TravelerPhantomEntity, Modelphantom<TravelerPhantomEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/traveler_phantom.png");

	public TravelerPhantomRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelphantom<TravelerPhantomEntity>(context.bakeLayer(Modelphantom.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(TravelerPhantomEntity entity) {
		return entityTexture;
	}
}