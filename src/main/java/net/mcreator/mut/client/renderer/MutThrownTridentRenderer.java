package net.mcreator.mut.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.mut.client.model.ModelMutTridentModel;
import net.mcreator.mut.entity.MutThrownTrident;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MutThrownTridentRenderer extends EntityRenderer<MutThrownTrident> {

    private static final Map<Item, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    static {
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.IRON_TRIDENT.get(),
                ResourceLocation.fromNamespaceAndPath("mut", "textures/block/iron_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.GOLDEN_TRIDENT.get(),
                ResourceLocation.fromNamespaceAndPath("mut", "textures/block/golden_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.DIAMOND_TRIDENT.get(),
                ResourceLocation.fromNamespaceAndPath("mut", "textures/block/diamond_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.NETHERITE_TRIDENT.get(),
                ResourceLocation.fromNamespaceAndPath("mut", "textures/block/netherite_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.WOODEN_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/wooden_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.STEEL_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/steel_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.GILDING_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/gilding_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.BLUE_DIAMOND_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/blue_diamond_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.ADVANCED_STEEL_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/advanced_steel_trident.png"));
        TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.COPPER_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/copper_trident.png"));
        //TEXTURE_MAP.put(net.mcreator.mut.init.MutModItems.X_TRIDENT.get(),ResourceLocation.fromNamespaceAndPath("mut", "textures/block/x_trident.png"));
    }

    private final ModelMutTridentModel<MutThrownTrident> model;

    public MutThrownTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelMutTridentModel<>(context.bakeLayer(ModelMutTridentModel.LAYER_LOCATION));
    }

    @Override
    public void render(MutThrownTrident entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getCustomStack();
        ResourceLocation texture = TEXTURE_MAP.getOrDefault(stack.getItem(),
                ResourceLocation.withDefaultNamespace("textures/entity/trident.png"));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer, RenderType.entitySolid(texture), false, entity.isFoil());
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MutThrownTrident entity) {
        ItemStack stack = entity.getCustomStack();
        return TEXTURE_MAP.getOrDefault(stack.getItem(),
                ResourceLocation.withDefaultNamespace("textures/entity/trident.png"));
    }
}