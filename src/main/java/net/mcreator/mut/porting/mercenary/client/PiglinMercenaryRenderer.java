package net.mcreator.mut.porting.mercenary.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.mercenary.PiglinMercenaryEntity;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * 猪灵雇佣兵渲染器：沿用原版猪灵模型，贴图单独一份便于替换。
 */
public class PiglinMercenaryRenderer
        extends HumanoidMobRenderer<PiglinMercenaryEntity, PiglinModel<PiglinMercenaryEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "textures/entity/piglin_mercenary.png");

    public PiglinMercenaryRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinModel<>(context.bakeLayer(ModelLayers.PIGLIN)),
                0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PIGLIN_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PIGLIN_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(PiglinMercenaryEntity entity) {
        return TEXTURE;
    }
}
