package net.mcreator.mut.porting.armorstand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.mcreator.mut.porting.armorstand.client.model.StaticWolfModel;
import net.mcreator.mut.porting.armorstand.client.model.WolfArmorStandBaseModel;
import net.mcreator.mut.porting.armorstand.WolfArmorStandEntity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 狼铠展示架渲染器：本体使用原版 WolfModel 的静态变体（尾巴不摇），
 * 狼铠渲染由自写 WolfArmorStandArmorLayer 完成（底图 + 染色 overlay
 * + 裂纹 + 纹饰内联）。
 */
public class WolfArmorStandRenderer
        extends MobRenderer<WolfArmorStandEntity, StaticWolfModel<WolfArmorStandEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "mut",
                    "textures/entity/wolf_armor_stand.png"
            );

    /**
     * 模型整体缩放：1.0 = 原版狼比例；调小整体变小。
     * GUI 预览走同一个渲染器会同步变小；碰撞箱不跟随，需另调 makeStandAABB。
     */
    private static final float RENDER_SCALE = 1.0F;

    public WolfArmorStandRenderer(
            EntityRendererProvider.Context context
    ) {
        super(
                context,
                new StaticWolfModel<>(context.bakeLayer(ModelLayers.WOLF)),
                0.0F
        );

        this.addLayer(
                new WolfArmorStandBaseLayer(
                        this,
                        new WolfArmorStandBaseModel<>(
                                context.bakeLayer(
                                        WolfArmorStandBaseModel.LAYER_LOCATION
                                )
                        )
                )
        );

        this.addLayer(
                new WolfArmorStandArmorLayer(
                        this,
                        context.getModelSet()
                )
        );
    }

    @Override
    public ResourceLocation getTextureLocation(
            WolfArmorStandEntity entity
    ) {
        return TEXTURE;
    }

    /**
     * 模型整体缩放：在 LivingEntityRenderer 的翻转之后、地面平移之前应用，
     * 缩放与平移同处一个坐标系，因此脚底仍然贴地。
     */
    @Override
    protected void scale(WolfArmorStandEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(RENDER_SCALE, RENDER_SCALE, RENDER_SCALE);
    }

    /**
     * 受击摇晃：同原版盔甲架，被击中后 5 tick 内绕 Y 轴小幅摆动。
     * 另将整个模型沿朝向前移 1 像素（1/16 格）。
     */
    @Override
    protected void setupRotations(
            WolfArmorStandEntity entity,
            PoseStack poseStack,
            float ageInTicks,
            float rotationYaw,
            float partialTicks,
            float scale
    ) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks, scale);

        // 沿朝向前移 1px：模型朝向为局部 -Z，故 Z 轴取负
        poseStack.translate(0.0F, 0.0F, -1.5F / 16.0F);

        float f = (float) (entity.level().getGameTime() - entity.lastHit) + partialTicks;
        if (f < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(f / 1.5F * (float) Math.PI) * 3.0F));
        }
    }
}