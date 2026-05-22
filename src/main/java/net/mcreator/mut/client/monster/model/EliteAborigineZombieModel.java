package net.mcreator.mut.client.monster.model;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import net.mcreator.mut.entity.EliteAborigineZombieEntity;

public class EliteAborigineZombieModel extends ZombieModel<EliteAborigineZombieEntity> {
    public EliteAborigineZombieModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(EliteAborigineZombieEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        boolean hasBow = mainHand.getItem() instanceof BowItem || offHand.getItem() instanceof BowItem;
        boolean hasCrossbow = mainHand.getItem() instanceof CrossbowItem || offHand.getItem() instanceof CrossbowItem;

        // ========== 弩装填姿势 ==========
        if (entity.isChargingCrossbow() && hasCrossbow) {
            this.rightArm.xRot = -0.8F;
            this.rightArm.yRot = 0.3F;
            this.leftArm.xRot = -0.8F;
            this.leftArm.yRot = -0.3F;
        }

        // ========== 弓拉弓姿势 ==========
        if (hasBow && entity.isUsingItem()) {
            int useTime = entity.getTicksUsingItem();
            float pullProgress = (float) useTime / 20.0F;
            if (pullProgress > 1.0F) pullProgress = 1.0F;

            this.rightArm.xRot = -1.5F + pullProgress * 0.5F;
            this.rightArm.yRot = 0.3F;
            this.rightArm.zRot = pullProgress * 0.2F;

            this.leftArm.xRot = -1.5F;
            this.leftArm.yRot = -0.3F - pullProgress * 0.3F;
            this.leftArm.zRot = 0.0F;
        }
    }
}