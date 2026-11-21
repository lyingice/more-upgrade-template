package net.mcreator.mut.porting.armorstand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.mcreator.mut.porting.armorstand.client.model.HorseArmorStandBaseModel;
import net.mcreator.mut.porting.armorstand.HorseArmorStandEntity;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 展示架渲染器：本体使用原版 HorseModel（马铠对齐原版形状），
 * 马铠由 {@link HorseArmorStandArmorLayer} 渲染——解析模组马铠贴图、
 * 保留染色 tint 与附魔光效，并叠加纹饰层。
 */
public class HorseArmorStandRenderer
        extends MobRenderer<HorseArmorStandEntity, HorseModel<HorseArmorStandEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "mut",
                    "textures/entity/horse_armor_stand.png"
            );

    public HorseArmorStandRenderer(
            EntityRendererProvider.Context context
    ) {
        super(
                context,
                new HorseModel<>(context.bakeLayer(ModelLayers.HORSE)),
                0.0F
        );

        this.addLayer(
                new HorseArmorStandBaseLayer(
                        this,
                        new HorseArmorStandBaseModel<>(
                                context.bakeLayer(
                                        HorseArmorStandBaseModel.LAYER_LOCATION
                                )
                        )
                )
        );

        // 自写铠层：模组马铠贴图解析 + 染色 tint + 附魔光效 + 纹饰
        // （原版 HorseArmorLayer 只会用 AnimalArmorItem 的固定贴图，模组马铠会显示成原版外观）
        this.addLayer(new HorseArmorStandArmorLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(
            HorseArmorStandEntity entity
    ) {
        return TEXTURE;
    }

    /**
     * 受击摇晃：同原版盔甲架，被击中后 5 tick 内绕 Y 轴小幅摆动。
     */
    @Override
    protected void setupRotations(
            HorseArmorStandEntity entity,
            PoseStack poseStack,
            float ageInTicks,
            float rotationYaw,
            float partialTicks,
            float scale
    ) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks, scale);

        float f = (float) (entity.level().getGameTime() - entity.lastHit) + partialTicks;
        if (f < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(f / 1.5F * (float) Math.PI) * 3.0F));
        }
    }
}