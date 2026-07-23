package net.mcreator.mut.client.renderer;

import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SlimeModel;

import net.mcreator.mut.entity.SteelSlimeEntity;
import net.minecraft.world.entity.monster.Slime;

public class SteelSlimeRenderer extends SlimeRenderer {
	private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/steel_slime.png");

	public SteelSlimeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
    @Override
    public ResourceLocation getTextureLocation(Slime entity) {
        return entityTexture;
    }
}