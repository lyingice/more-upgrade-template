package net.mcreator.mut.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.mut.item.*;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CustomElytraLayer extends ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final ElytraModel<AbstractClientPlayer> elytraModel;

    public CustomElytraLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
                             EntityModelSet modelSet) {
        super(renderer, modelSet);
        this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw,
                       float headPitch) {
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        ResourceLocation texture = getCustomTexture(chestStack);

        if (texture != null) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.125F);
            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            RenderType renderType = this.elytraModel.renderType(texture);
            this.elytraModel.renderToBuffer(poseStack,
                    ItemRenderer.getArmorFoilBuffer(buffer, renderType, chestStack.hasFoil()),
                    packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        } else {
            super.render(poseStack, buffer, packedLight, player, limbSwing, limbSwingAmount,
                    partialTick, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    public boolean shouldRender(ItemStack stack, AbstractClientPlayer player) {
        return getCustomTexture(stack) != null || super.shouldRender(stack, player);
    }

    private ResourceLocation getCustomTexture(ItemStack stack) {
        if (stack.getItem() instanceof IronElytraItem) {
            return ResourceLocation.parse("mut:textures/entities/iron_elytra.png");
        } else if (stack.getItem() instanceof GoldenElytraItem) {
            return ResourceLocation.parse("mut:textures/entities/golden_elytra.png");
        } else if (stack.getItem() instanceof DiamondElytraItem) {
            return ResourceLocation.parse("mut:textures/entities/diamond_elytra.png");
        } else if (stack.getItem() instanceof NetheriteElytraItem) {
            return ResourceLocation.parse("mut:textures/entities/netherite_elytra.png");
        } else if (stack.getItem() instanceof DragonChestplateElytraItem) {
            return ResourceLocation.parse("mut:textures/entities/dragon_elytra.png");
        }else if (stack.getItem() instanceof CopperElytraItem) {    return ResourceLocation.parse("mut:textures/entities/copper_elytra.png");}
        else if (stack.getItem() instanceof EmeraldElytraItem) {    return ResourceLocation.parse("mut:textures/entities/emerald_elytra.png");}
        else if (stack.getItem() instanceof LapisLazuliElytraItem) {    return ResourceLocation.parse("mut:textures/entities/lapis_lazuli_elytra.png");}
        else if (stack.getItem() instanceof ObsidianElytraItem) {    return ResourceLocation.parse("mut:textures/entities/obsidian_elytra.png");}
        else if (stack.getItem() instanceof NetherStarElytraItem) {    return ResourceLocation.parse("mut:textures/entities/nether_star_elytra.png");}
        else if (stack.getItem() instanceof DragonElytraItem) {    return ResourceLocation.parse("mut:textures/entities/dragon_elytra.png");}
        else if (stack.getItem() instanceof PoisonSteelElytraItem) {    return ResourceLocation.parse("mut:textures/entities/poison_steel_elytra.png");}
        else if (stack.getItem() instanceof AmethystElytraItem) {    return ResourceLocation.parse("mut:textures/entities/amethyst_elytra.png");}

        return null;
    }
}