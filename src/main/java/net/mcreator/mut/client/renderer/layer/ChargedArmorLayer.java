package net.mcreator.mut.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.mut.item.armor.IChargedArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ChargedArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ModelLayerLocation CHARGED_ARMOR_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("mut", "charged_armor"), "main");

    private final HumanoidModel<T> chargedModel;

    public ChargedArmorLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.chargedModel = new HumanoidModel<>(modelSet.bakeLayer(CHARGED_ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof IChargedArmor charged)) continue;

            ResourceLocation texture = charged.getEnergyTexture();
            if (texture == null) continue;

            // 同步属性
            this.getParentModel().copyPropertiesTo(chargedModel);

            // 只显示对应部位
            setAllInvisible(chargedModel);
            switch (slot) {
                case HEAD -> chargedModel.head.visible = true;
                case CHEST -> {
                    chargedModel.body.visible = true;
                    chargedModel.leftArm.visible = true;
                    chargedModel.rightArm.visible = true;
                }
                case LEGS -> {
                    chargedModel.body.visible = true;
                    chargedModel.leftLeg.visible = true;
                    chargedModel.rightLeg.visible = true;
                }
                case FEET -> {
                    chargedModel.leftLeg.visible = true;
                    chargedModel.rightLeg.visible = true;
                }
            }

            // UV流动
            float uvOffset = (entity.tickCount + partialTicks) * 0.005F;

            VertexConsumer vertexConsumer = buffer.getBuffer(
                    RenderType.energySwirl(texture, uvOffset, uvOffset * 0.5F)
            );

            chargedModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        }
    }

    private void setAllInvisible(HumanoidModel<T> model) {
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.leftArm.visible = false;
        model.rightArm.visible = false;
        model.leftLeg.visible = false;
        model.rightLeg.visible = false;
    }
}