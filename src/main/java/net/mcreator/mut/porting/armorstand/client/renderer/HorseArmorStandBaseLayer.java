package net.mcreator.mut.porting.armorstand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.mcreator.mut.porting.armorstand.client.model.HorseArmorStandBaseModel;
import net.mcreator.mut.porting.armorstand.HorseArmorStandEntity;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * 展示架底座渲染层：在假马本体下方渲染木质底座，
 * 贴图复用 horse_armor_stand.png 的底座区域。
 */
public class HorseArmorStandBaseLayer
        extends RenderLayer<
        HorseArmorStandEntity,
        HorseModel<HorseArmorStandEntity>
        > {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "mut",
                    "textures/entity/horse_armor_stand.png"
            );

    private final HorseArmorStandBaseModel<HorseArmorStandEntity> baseModel;

    public HorseArmorStandBaseLayer(
            RenderLayerParent<
                    HorseArmorStandEntity,
                    HorseModel<HorseArmorStandEntity>
                    > parent,
            HorseArmorStandBaseModel<HorseArmorStandEntity> baseModel
    ) {
        super(parent);
        this.baseModel = baseModel;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            HorseArmorStandEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityCutoutNoCull(TEXTURE)
        );

        this.baseModel.renderToBuffer(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                -1
        );
    }
}