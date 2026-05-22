package net.mcreator.mut.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import net.mcreator.mut.entity.RedLightningCreeperEntity;
import net.mcreator.mut.client.model.Modelcreeper_charge;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class RedLightningCreeperRenderer extends MobRenderer<RedLightningCreeperEntity, CreeperModel<RedLightningCreeperEntity>> {
    private final ResourceLocation entityTexture = ResourceLocation.parse("mut:textures/entities/creeper.png");
    private static final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("mut:textures/entities/creeper_charge.png");

    public RedLightningCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel<RedLightningCreeperEntity>(context.bakeLayer(ModelLayers.CREEPER)), 0.5f);
        this.addLayer(new RenderLayer<RedLightningCreeperEntity, CreeperModel<RedLightningCreeperEntity>>(this) {

            @Override
            public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, RedLightningCreeperEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

                // 计算UV偏移量
                float uvOffset = (entity.tickCount + partialTicks) * 0.01F;

                // 使用 energy_swirl 渲染类型
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(LAYER_TEXTURE, uvOffset, uvOffset * 0.5F));

                EntityModel model = new Modelcreeper_charge(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcreeper_charge.LAYER_LOCATION));
                this.getParentModel().copyPropertiesTo(model);
                model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                model.renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0));
            }
        });
    }

    // ========== 膨胀缩放 + 抖动 ==========
    @Override
    protected void scale(RedLightningCreeperEntity entity, PoseStack poseStack, float partialTicks) {
        float swelling = entity.getSwelling(partialTicks);
        float wobble = 1.0F + Mth.sin(swelling * 100.0F) * swelling * 0.01F;
        swelling = Mth.clamp(swelling, 0.0F, 1.0F);
        swelling *= swelling;   // g²
        swelling *= swelling;   // g⁴
        float s = (1.0F + swelling * 0.4F) * wobble;   // 水平放大40%
        float hs = (1.0F + swelling * 0.1F) / wobble;  // 垂直压缩
        poseStack.scale(s, hs, s);
    }

    // ========== 膨胀变白闪烁 ==========
    @Override
    protected float getWhiteOverlayProgress(RedLightningCreeperEntity entity, float partialTicks) {
        float swelling = entity.getSwelling(partialTicks);
        return (int) (swelling * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swelling, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(RedLightningCreeperEntity entity) {
        return entityTexture;
    }
}