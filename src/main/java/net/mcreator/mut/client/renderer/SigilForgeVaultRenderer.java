package net.mcreator.mut.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.core.Direction;

import net.mcreator.mut.block.entity.SigilForgeVaultCommonBlockEntity;

public class SigilForgeVaultRenderer implements BlockEntityRenderer<SigilForgeVaultCommonBlockEntity> {

    private final ItemRenderer itemRenderer;

    public SigilForgeVaultRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SigilForgeVaultCommonBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack displayItem = blockEntity.getDisplayItem();
        if (displayItem.isEmpty()) return;

        float spin = blockEntity.getDisplaySpin(partialTick);

        poseStack.pushPose();

        // 中心点
        poseStack.translate(0.5, 0.5, 0.5);

        // 旋转物品
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        // 缩小
        poseStack.scale(0.5f, 0.5f, 0.5f);

        itemRenderer.renderStatic(
                displayItem,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0
        );

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 32;
    }
}