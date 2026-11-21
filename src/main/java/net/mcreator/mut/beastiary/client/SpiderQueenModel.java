package net.mcreator.mut.beastiary.client;

import net.mcreator.mut.beastiary.SpiderQueenEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 蜘蛛女王的独立模型。
 * <p>
 * 主体几何完全沿用原版蜘蛛（同样的 UV、同样的八条腿），只额外加了两处标识：
 * 头顶一对尖角、腹部末端一个卵囊。贴图用 128×128，左上 64×32 就是原版蜘蛛布局，
 * 所以躯干和腿的贴图能对得上，不会糊成一片。
 * <p>
 * 动画：原版八腿步态 + 待机呼吸 + 攻击前倾 + 卵囊脉动。
 */
public class SpiderQueenModel extends HierarchicalModel<SpiderQueenEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body0;
    private final ModelPart body1;
    private final ModelPart sac;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightMiddleHindLeg;
    private final ModelPart leftMiddleHindLeg;
    private final ModelPart rightMiddleFrontLeg;
    private final ModelPart leftMiddleFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public SpiderQueenModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body0 = root.getChild("body0");
        this.body1 = root.getChild("body1");
        this.sac = root.getChild("body1").getChild("sac");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightMiddleHindLeg = root.getChild("right_middle_hind_leg");
        this.leftMiddleHindLeg = root.getChild("left_middle_hind_leg");
        this.rightMiddleFrontLeg = root.getChild("right_middle_front_leg");
        this.leftMiddleFrontLeg = root.getChild("left_middle_front_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ===== 以下几何与原版蜘蛛逐字对齐（64×32 区）=====
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 15.0F, -3.0F));

        // 头顶尖角（挂在头下，跟着头一起转）
        head.addOrReplaceChild("crown",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.5F, -6.0F, -7.0F, 2.0F, 4.0F, 2.0F)
                        .texOffs(0, 32).addBox(2.5F, -6.0F, -7.0F, 2.0F, 4.0F, 2.0F),
                PartPose.ZERO);

        root.addOrReplaceChild("body0",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition body1 = root.addOrReplaceChild("body1",
                CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F),
                PartPose.offset(0.0F, 15.0F, 9.0F));

        // 腹部末端的卵囊（挂在腹部下，跟着腹部动）
        body1.addOrReplaceChild("sac",
                CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 8.0F),
                PartPose.offset(0.0F, 1.0F, 11.0F));

        CubeListBuilder right = CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
        CubeListBuilder left = CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
        root.addOrReplaceChild("right_hind_leg", right, PartPose.offset(-4.0F, 15.0F, 2.0F));
        root.addOrReplaceChild("left_hind_leg", left, PartPose.offset(4.0F, 15.0F, 2.0F));
        root.addOrReplaceChild("right_middle_hind_leg", right, PartPose.offset(-4.0F, 15.0F, 1.0F));
        root.addOrReplaceChild("left_middle_hind_leg", left, PartPose.offset(4.0F, 15.0F, 1.0F));
        root.addOrReplaceChild("right_middle_front_leg", right, PartPose.offset(-4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("left_middle_front_leg", left, PartPose.offset(4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("right_front_leg", right, PartPose.offset(-4.0F, 15.0F, -1.0F));
        root.addOrReplaceChild("left_front_leg", left, PartPose.offset(4.0F, 15.0F, -1.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(SpiderQueenEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // ---- 原版蜘蛛的步态（数值逐字抄自 SpiderModel）----
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        float f = (float) (Math.PI / 4);
        this.rightHindLeg.zRot = -f;
        this.leftHindLeg.zRot = f;
        this.rightMiddleHindLeg.zRot = -0.58119464F;
        this.leftMiddleHindLeg.zRot = 0.58119464F;
        this.rightMiddleFrontLeg.zRot = -0.58119464F;
        this.leftMiddleFrontLeg.zRot = 0.58119464F;
        this.rightFrontLeg.zRot = -f;
        this.leftFrontLeg.zRot = f;

        float f1 = (float) (-Math.PI / 8);   // -22.5°
        float f2 = (float) (Math.PI / 4);
        this.rightHindLeg.yRot = f1;
        this.leftHindLeg.yRot = -f1;
        this.rightMiddleHindLeg.yRot = f1;
        this.leftMiddleHindLeg.yRot = -f1;
        this.rightMiddleFrontLeg.yRot = -f1;
        this.leftMiddleFrontLeg.yRot = f1;
        this.rightFrontLeg.yRot = -f2;
        this.leftFrontLeg.yRot = f2;

        float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F) * 0.4F) * limbSwingAmount;
        float f4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float f5 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbSwingAmount;
        float f6 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbSwingAmount;
        float f7 = Math.abs(Mth.sin(limbSwing * 0.6662F) * 0.4F) * limbSwingAmount;
        float f8 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float f9 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbSwingAmount;
        float f10 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbSwingAmount;

        this.rightHindLeg.yRot += f3;
        this.leftHindLeg.yRot -= f3;
        this.rightMiddleHindLeg.yRot += f4;
        this.leftMiddleHindLeg.yRot -= f4;
        this.rightMiddleFrontLeg.yRot += f5;
        this.leftMiddleFrontLeg.yRot -= f5;
        this.rightFrontLeg.yRot += f6;
        this.leftFrontLeg.yRot -= f6;
        this.rightHindLeg.zRot += f7;
        this.leftHindLeg.zRot -= f7;
        this.rightMiddleHindLeg.zRot += f8;
        this.leftMiddleHindLeg.zRot -= f8;
        this.rightMiddleFrontLeg.zRot += f9;
        this.leftMiddleFrontLeg.zRot -= f9;
        this.rightFrontLeg.zRot += f10;
        this.leftFrontLeg.zRot -= f10;

        // ---- 女王自己的细节 ----
        // 待机呼吸：身体轻微起伏
        float breathe = Mth.sin(ageInTicks * 0.1F) * 0.35F;
        this.body1.y = 15.0F + breathe;

        // 卵囊脉动
        float pulse = 1.0F + Mth.sin(ageInTicks * 0.12F) * 0.08F;
        this.sac.xScale = pulse;
        this.sac.yScale = pulse;
        this.sac.zScale = pulse;

        // 攻击瞬间：头下压、腹部微抬
        float attack = entity.attackAnim;
        if (attack > 0.0F) {
            float lunge = Mth.sin(attack * (float) Math.PI);
            this.head.xRot += lunge * 0.45F;
            this.body1.xRot = -lunge * 0.12F;
        } else {
            this.body1.xRot = 0.0F;
        }
    }
}
