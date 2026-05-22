package net.mcreator.mut.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;

import net.mcreator.mut.entity.LittleCreeperEntity;
import net.mcreator.mut.client.model.Modelcreeper;

public class LittleCreeperRenderer extends MobRenderer<LittleCreeperEntity, Modelcreeper<LittleCreeperEntity>> {
    private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/creeper.png");

    public LittleCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new Modelcreeper<LittleCreeperEntity>(context.bakeLayer(Modelcreeper.LAYER_LOCATION)), 0.4f);
    }

    // ========== 膨胀缩放 + 抖动 ==========
    @Override
    protected void scale(LittleCreeperEntity entity, PoseStack poseStack, float partialTicks) {
        float swelling = entity.getSwelling(partialTicks);
        float wobble = 1.0F + Mth.sin(swelling * 100.0F) * swelling * 0.01F;
        swelling = Mth.clamp(swelling, 0.0F, 1.0F);
        swelling *= swelling;
        swelling *= swelling;
        float s = (1.0F + swelling * 0.4F) * wobble;
        float hs = (1.0F + swelling * 0.1F) / wobble;
        poseStack.scale(s, hs, s);
    }

    // ========== 膨胀变白闪烁 ==========
    @Override
    protected float getWhiteOverlayProgress(LittleCreeperEntity entity, float partialTicks) {
        float swelling = entity.getSwelling(partialTicks);
        return (int) (swelling * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swelling, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(LittleCreeperEntity entity) {
        return entityTexture;
    }
}