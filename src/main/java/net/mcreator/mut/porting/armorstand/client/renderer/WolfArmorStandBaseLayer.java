package net.mcreator.mut.porting.armorstand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.mcreator.mut.porting.armorstand.client.model.StaticWolfModel;
import net.mcreator.mut.porting.armorstand.client.model.WolfArmorStandBaseModel;
import net.mcreator.mut.porting.armorstand.WolfArmorStandEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * 展示架底座渲染层：在假狼本体下方渲染木质底座，
 * 贴图使用 wolf_armor_stand_base.png。
 */
public class WolfArmorStandBaseLayer
        extends RenderLayer<
        WolfArmorStandEntity,
        StaticWolfModel<WolfArmorStandEntity>
        > {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "mut",
                    "textures/entity/wolf_armor_stand_base.png"
            );

    private final WolfArmorStandBaseModel<WolfArmorStandEntity> baseModel;

    public WolfArmorStandBaseLayer(
            RenderLayerParent<
                    WolfArmorStandEntity,
                    StaticWolfModel<WolfArmorStandEntity>
                    > parent,
            WolfArmorStandBaseModel<WolfArmorStandEntity> baseModel
    ) {
        super(parent);
        this.baseModel = baseModel;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            WolfArmorStandEntity entity,
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