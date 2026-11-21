package net.mcreator.mut.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.mut.client.entities.ModelCryingObsidianTrident;
import net.mcreator.mut.client.model.ModelMutTridentModel;
import net.mcreator.mut.entity.MutThrownTrident;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MutThrownTridentRenderer extends EntityRenderer<MutThrownTrident> {

    // ★ 需要特殊模型的三叉戟
    private static final Map<Item, EntityModel<MutThrownTrident>> CUSTOM_MODELS = new HashMap<>();

    private final ModelMutTridentModel<MutThrownTrident> defaultModel;

    public MutThrownTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.defaultModel = new ModelMutTridentModel<>(
                context.bakeLayer(ModelMutTridentModel.LAYER_LOCATION));

        // ★ 注册特殊模型
        CUSTOM_MODELS.put(MutModItems.CRYING_OBSIDIAN_TRIDENT.get(),
                new ModelCryingObsidianTrident(
                        context.bakeLayer(ModelCryingObsidianTrident.LAYER_LOCATION)));
    }

    @Override
    public void render(MutThrownTrident entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getCustomStack();
        if (stack.isEmpty()) return;

        EntityModel<MutThrownTrident> model = CUSTOM_MODELS.getOrDefault(
                stack.getItem(), defaultModel);

        ResourceLocation tex = CUSTOM_MODELS.containsKey(stack.getItem())
                ? deriveTexture(stack.getItem())   // 特殊模型也用自动纹理
                : deriveTexture(stack.getItem());  // 默认模型自动纹理

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));

        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, RenderType.entitySolid(tex), false, entity.isFoil());
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static ResourceLocation deriveTexture(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                "textures/block/" + id.getPath() + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(MutThrownTrident entity) {
        return deriveTexture(entity.getCustomStack().getItem());
    }
}