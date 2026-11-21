package net.mcreator.mut.client.entities;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelCryingObsidianTrident<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("mut", "crying_obsidian_trident"), "main");

    private final ModelPart body;

    public ModelCryingObsidianTrident(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 0).addBox(-2.5F, 0.1F, -0.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 0).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition group2 = body.addOrReplaceChild("group2",
                CubeListBuilder.create()
                        .texOffs(24, 11).addBox(0.0F, -23.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 11).addBox(-1.0F, -23.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 19.0F, 0.0F));

        group2.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(17, 14).addBox(-1.0F, -0.4F, -0.5F, 1.4F, 1.4F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -23.4142F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition group3 = body.addOrReplaceChild("group3",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 27.0F, 0.0F));

        PartDefinition group4 = group3.addOrReplaceChild("group4",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        group4.addOrReplaceChild("group5",
                CubeListBuilder.create()
                        .texOffs(24, 11).addBox(2.4893F, -29.9779F, -0.49F, 1.0F, 4.0F, 0.98F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        group4.addOrReplaceChild("group6",
                CubeListBuilder.create()
                        .texOffs(14, 17).addBox(2.5105F, -26.9802F, -0.49F, 1.0F, 3.0F, 0.98F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition group7 = group3.addOrReplaceChild("group7",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        group7.addOrReplaceChild("group8",
                CubeListBuilder.create()
                        .texOffs(24, 11).addBox(-3.4893F, -29.9779F, -0.49F, 1.0F, 4.0F, 0.98F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        group7.addOrReplaceChild("group9",
                CubeListBuilder.create()
                        .texOffs(14, 17).addBox(-3.5105F, -26.9802F, -0.49F, 1.0F, 3.0F, 0.98F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition group10 = body.addOrReplaceChild("group10",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        group10.addOrReplaceChild("cube_r2",
                CubeListBuilder.create()
                        .texOffs(15, 9).addBox(-1.5F, -1.5F, -0.55F, 3.0F, 3.0F, 1.1F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition group = body.addOrReplaceChild("group",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        group.addOrReplaceChild("cube_r3",
                CubeListBuilder.create()
                        .texOffs(15, 3).addBox(-2.5F, -1.5F, -0.55F, 4.0F, 4.0F, 1.1F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}