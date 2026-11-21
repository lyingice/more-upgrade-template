package net.mcreator.mut.porting.armorstand.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * 展示架木质底座。UV 使用 wolf_armor_stand.png 的 (0,0) 区域。
 */
public class WolfArmorStandBaseModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(
                            "mut",
                            "wolf_armor_stand_base"
                    ),
                    "main"
            );

    private final ModelPart base;

    public WolfArmorStandBaseModel(ModelPart root) {
        this.base = root.getChild("Base");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild(
                "Base",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -6.0F, -2F, -7.5F,
                                10.0F, 1.0F, 15.0F,
                                new CubeDeformation(0.0F)
                        ),
                // Z 偏移：+ 为向后（狼头朝 -Z），1 = 后移 1 像素
                PartPose.offset(1.0F, 24.0F, 1.4F)
        );

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        // 静态底座，无动画
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}