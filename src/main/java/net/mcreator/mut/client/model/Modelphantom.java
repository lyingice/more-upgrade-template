package net.mcreator.mut.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelphantom<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.parse("mut:phantom"), "main");
	// 原版幻翼的拍打频率
	public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
	public final ModelPart body;
	public final ModelPart head;
	public final ModelPart left_wing;
	public final ModelPart left_wing_tip;
	public final ModelPart right_wing;
	public final ModelPart right_wing_tip;
	public final ModelPart tail;
	public final ModelPart tail2;

	public Modelphantom(ModelPart root) {
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.left_wing = root.getChild("left_wing");
		this.left_wing_tip = this.left_wing.getChild("left_wing_tip");
		this.right_wing = root.getChild("right_wing");
		this.right_wing_tip = this.right_wing.getChild("right_wing_tip");
		this.tail = root.getChild("tail");
		this.tail2 = root.getChild("tail2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 19.0F, 0.0F));
		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 20.25F, -8.0F));
		PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(23, 12).addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 17.0F, -8.0F));
		PartDefinition left_wing_tip = left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(16, 24).addBox(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
		PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(23, 12).mirror().addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(-2.5F, 17.0F, -8.0F));
		PartDefinition right_wing_tip = right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(16, 24).mirror().addBox(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(-6.0F, 0.0F, 0.0F));
		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(3, 20).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 17.0F, 1.0F));
		PartDefinition tail2 = partdefinition.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(4, 29).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 17.5F, 7.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// === 翅膀拍打动画 ===
		float flapAngle = Mth.cos(ageInTicks * FLAP_DEGREES_PER_TICK * ((float) Math.PI / 180F)) * (float) Math.PI * 0.25F;
		// 左翅主体：绕Z轴上下拍打
		this.left_wing.zRot = -flapAngle;
		// 左翅尖：在父组末端再叠加弯曲（符号相反形成自然弯曲）
		this.left_wing_tip.zRot = flapAngle * 0.3F;
		// 右翅主体：对称
		this.right_wing.zRot = flapAngle;
		// 右翅尖
		this.right_wing_tip.zRot = -flapAngle * 0.3F;
		// === 尾巴摆动 ===
		float tailBob = Mth.cos(ageInTicks * 0.2F) * 0.15F;
		this.tail.xRot = tailBob;
		this.tail2.xRot = tailBob * 0.5F;
		// === 头部朝向 ===
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		tail2.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
	}
}