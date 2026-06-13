package net.mcreator.mut.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.mcreator.mut.block.entity.SigilForgeBossTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.SigilForgeTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.SigilForgeUniqueTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.boss_spawner.BossSpawnerState;
import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerState;

public class MutTrialSpawnerRenderer implements BlockEntityRenderer<BlockEntity> {

    private final EntityRenderDispatcher entityRenderer;

    public MutTrialSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.getEntityRenderer();
    }

    @Override
    public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Level level = blockEntity.getLevel();
        if (level == null) return;

        EntityType<?> entityType = null;
        float spin = (level.getGameTime() + partialTick) * 2.0f;

        if (blockEntity instanceof SigilForgeTrialSpawnerBlockEntity spawner) {
            entityType = spawner.getMobType();
        } else if (blockEntity instanceof SigilForgeUniqueTrialSpawnerBlockEntity spawner) {
            entityType = spawner.getMobType();
        } else if (blockEntity instanceof SigilForgeBossTrialSpawnerBlockEntity spawner) {
            entityType = spawner.getBossType();
            spin = (level.getGameTime() + partialTick) * 0.5f;
        }

        if (entityType == null) return;

        Entity entity = entityType.create(level);
        if (entity == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.15, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        float scale = 0.4f;
        if (entityType == EntityType.WITHER || entityType == EntityType.WARDEN) scale = 0.25f;
        if (entityType == EntityType.ENDER_DRAGON) scale = 0.15f;
        poseStack.scale(scale, scale, scale);

        entityRenderer.render(entity, 0, 0, 0, 0, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(BlockEntity blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(
                pos.getX() - 1.0, pos.getY() - 1.0, pos.getZ() - 1.0,
                pos.getX() + 2.0, pos.getY() + 2.0, pos.getZ() + 2.0);
    }
}