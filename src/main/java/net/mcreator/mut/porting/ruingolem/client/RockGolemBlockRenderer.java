package net.mcreator.mut.porting.ruingolem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.mut.porting.ruingolem.RockGolemBlockEntity;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.animal.IronGolem;

/**
 * 石像方块的渲染：用铁傀儡的模型骨架 + 岩石傀儡的贴图，
 * 和活动状态下的傀儡完全一致，站着不动时两者看不出区别。
 */
public class RockGolemBlockRenderer implements BlockEntityRenderer<RockGolemBlockEntity> {

    private final IronGolemModel<IronGolem> model;

    public RockGolemBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new IronGolemModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM));
    }

    @Override
    public void render(RockGolemBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer consumer = bufferSource.getBuffer(
                RenderType.entityCutoutNoCull(RuinGolemRenderers.ROCK_TEXTURE));
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
