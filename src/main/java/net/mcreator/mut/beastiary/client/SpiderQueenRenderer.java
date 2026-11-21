package net.mcreator.mut.beastiary.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.beastiary.SpiderQueenEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** 蜘蛛女王渲染器：独立模型 + 放大到 3 倍体型。 */
public class SpiderQueenRenderer extends MobRenderer<SpiderQueenEntity, SpiderQueenModel> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "spider_queen"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "textures/entity/spider_queen.png");

    /** 模型按原版蜘蛛比例搭的，整体放大到 3 倍以匹配 4.8×3.0 的碰撞箱 */
    private static final float MODEL_SCALE = 3.0F;

    public SpiderQueenRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderQueenModel(context.bakeLayer(LAYER)), 1.2F);
    }

    @Override
    protected void scale(SpiderQueenEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderQueenEntity entity) {
        return TEXTURE;
    }
}
