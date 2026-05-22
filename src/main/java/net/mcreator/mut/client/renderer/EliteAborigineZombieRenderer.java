package net.mcreator.mut.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.ZombieModel;

import net.mcreator.mut.entity.EliteAborigineZombieEntity;
import net.mcreator.mut.client.monster.model.EliteAborigineZombieModel;

public class EliteAborigineZombieRenderer extends HumanoidMobRenderer<EliteAborigineZombieEntity, EliteAborigineZombieModel> {
    private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/aborigine_zombie.png");

    public EliteAborigineZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new EliteAborigineZombieModel(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(EliteAborigineZombieEntity entity) {
        return entityTexture;
    }
}