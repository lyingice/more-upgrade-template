package net.mcreator.mut.block;

import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import javax.annotation.Nullable;

public class SigilForgeGrassBlock extends GrassBlock {

    public SigilForgeGrassBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.WET_GRASS)
                .strength(0.6f, 10f)
                .randomTicks()
        );
    }

    // ========== 完全覆写 randomTick，控制扩散逻辑 ==========
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 检查是否能在当前环境存活（亮度 + 土壤）
        if (!canSurviveInDimension(level, pos)) {
            // 无法存活，退化为符锻韧土
            level.setBlockAndUpdate(pos, MutModBlocks.SIGIL_FORGE_DIRT.get().defaultBlockState());
            return;
        }

        // 检查亮度是否足够扩散
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            // 尝试扩散
            spreadGrass(level, pos, random);
        }
    }

    // ========== 扩散逻辑：只在符锻韧土上扩散 ==========
    private void spreadGrass(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState grassState = this.defaultBlockState();

        for (int i = 0; i < 4; i++) {
            BlockPos targetPos = pos.offset(
                    random.nextInt(3) - 1,
                    random.nextInt(5) - 3,
                    random.nextInt(3) - 1
            );

            BlockState targetState = level.getBlockState(targetPos);

            // 检查目标是否是符锻韧土，且可以传播
            if (targetState.getBlock() instanceof SigilForgeDirtBlock &&
                    canPropagateTo(level, targetPos)) {

                // 将符锻韧土转化为符锻草方块（根据上方是否有雪设置 SNOWY 属性）
                level.setBlockAndUpdate(targetPos,
                        grassState.setValue(SNOWY, level.getBlockState(targetPos.above()).is(Blocks.SNOW))
                );
                break;
            }
        }
    }

    // ========== 检查是否可以传播到目标位置 ==========
    private boolean canPropagateTo(ServerLevel level, BlockPos pos) {
        // 检查上方是否被水淹没
        if (level.getFluidState(pos.above()).is(FluidTags.WATER)) {
            return false;
        }

        // 检查光照条件（复刻原版逻辑）
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        int lightBlock = LightEngine.getLightBlockInto(
                level,
                this.defaultBlockState(),
                pos,
                aboveState,
                abovePos,
                Direction.UP,
                aboveState.getLightBlock(level, abovePos)
        );

        return lightBlock < level.getMaxLightLevel();
    }

    // ========== 检查在维度中是否能存活 ==========
    private boolean canSurviveInDimension(ServerLevel level, BlockPos pos) {
        // 只检查光照（完全无视下方方块）
        return canBeGrassLike(level, pos);
    }

    // ========== 复制原版的 canBeGrass 逻辑 ==========
    private boolean canBeGrassLike(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // 如果上方是雪（1层），可以存活
        if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        }

        // 如果上方是水（满格），不能存活
        if (aboveState.getFluidState().getAmount() == 8) {
            return false;
        }

        // 检查光照
        int lightBlock = LightEngine.getLightBlockInto(
                level,
                this.defaultBlockState(),
                pos,
                aboveState,
                abovePos,
                Direction.UP,
                aboveState.getLightBlock(level, abovePos)
        );

        return lightBlock < level.getMaxLightLevel();
    }

    // ========== 骨粉效果 ==========
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos abovePos = pos.above();

        for (int i = 0; i < 64; i++) {
            BlockPos targetPos = abovePos.offset(
                    random.nextInt(5) - 2,
                    random.nextInt(3) - 1,
                    random.nextInt(5) - 2
            );

            BlockState belowState = level.getBlockState(targetPos.below());
            if (!(belowState.getBlock() instanceof SigilForgeGrassBlock) &&
                    !(belowState.getBlock() instanceof SigilForgeDirtBlock)) {
                continue;
            }

            if (level.getBlockState(targetPos).isAir()) {
                // 生成装饰植物（可替换为自定义植被）
                level.setBlock(targetPos, Blocks.SHORT_GRASS.defaultBlockState(), 3);
                break;
            }
        }
    }

    // ========== 破坏时掉落符锻韧土 ==========
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide && !player.isCreative()) {
            Block.popResource(level, pos, new ItemStack(MutModBlocks.SIGIL_FORGE_DIRT.get()));
        }
    }
}