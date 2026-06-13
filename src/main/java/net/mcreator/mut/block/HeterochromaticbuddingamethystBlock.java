package net.mcreator.mut.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class HeterochromaticbuddingamethystBlock extends BuddingAmethystBlock {

    private static final Direction[] DIRECTIONS = Direction.values();
    public static final Block[] CLUSTERS = new Block[4];

    public HeterochromaticbuddingamethystBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.AMETHYST)
                .strength(10f)
                .requiresCorrectToolForDrops()
                .randomTicks());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);

            if (canClusterGrowAtState(targetState)) {
                Block stage1 = CLUSTERS[0];
                if (stage1 != null) {
                    level.setBlockAndUpdate(targetPos, stage1.defaultBlockState()
                            .setValue(AmethystClusterBlock.FACING, direction)
                            .setValue(AmethystClusterBlock.WATERLOGGED,
                                    targetState.getFluidState().getType() == Fluids.WATER));
                }
            } else if (isYourCluster(targetState) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                int currentStage = getClusterStage(targetState.getBlock());
                if (currentStage >= 0 && currentStage < 3) {
                    Block nextBlock = CLUSTERS[currentStage + 1];
                    if (nextBlock != null) {
                        level.setBlockAndUpdate(targetPos, nextBlock.defaultBlockState()
                                .setValue(AmethystClusterBlock.FACING, direction)
                                .setValue(AmethystClusterBlock.WATERLOGGED,
                                        targetState.getValue(AmethystClusterBlock.WATERLOGGED)));
                    }
                }
            }
        }
    }

    private boolean isYourCluster(BlockState state) {
        Block block = state.getBlock();
        for (Block b : CLUSTERS) {
            if (b == block) return true;
        }
        return false;
    }

    private int getClusterStage(Block block) {
        for (int i = 0; i < CLUSTERS.length; i++) {
            if (CLUSTERS[i] == block) return i;
        }
        return -1;
    }
}