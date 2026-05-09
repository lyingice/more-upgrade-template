package net.mcreator.mut.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class ModelMutTridentModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("mut", "model_mut_trident_model"), "main");
	public final ModelPart body;
	public final ModelPart base;
	public final ModelPart left_spike;
	public final ModelPart middle_spike;
	public final ModelPart right_spike;

	public ModelMutTridentModel(ModelPart root) {
		this.body = root.getChild("body");
		this.base = root.getChild("base");
		this.left_spike = root.getChild("left_spike");
		this.middle_spike = root.getChild("middle_spike");
		this.right_spike = root.getChild("right_spike");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.offset(0.0F, -3.0F, 0.0F));
		partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -3.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_spike", CubeListBuilder.create().texOffs(4, 3).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, -3.0F, 0.0F));
		partdefinition.addOrReplaceChild("middle_spike", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, -3.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_spike", CubeListBuilder.create().texOffs(4, 3).mirror().addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, -3.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		left_spike.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		middle_spike.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		right_spike.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}