package net.mcreator.mut.block;

import net.mcreator.mut.block.MutBlock.Blocks.*;
import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LeavesBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class DebrisLeavesBlock extends LeavesBlock {
	public DebrisLeavesBlock() {
		super(BlockBehaviour.Properties.of()
                .sound(SoundType.AZALEA_LEAVES)
                .strength(1f, 10f)
                .noOcclusion().pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor((bs, br, bp) -> false)
                .ignitedByLava().isSuffocating((bs, br, bp) -> false)
				.isViewBlocking((bs, br, bp) -> false)
                .randomTicks()
                .mapColor(MapColor.COLOR_GREEN)
        );


        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, 7)
                .setValue(PERSISTENT, false)
                .setValue(WATERLOGGED, false)
                .setValue(CAN_BEAR_FRUIT, true)  // 默认可以结果
                .setValue(FRUIT_COOLDOWN, 0)  // 默认冷却为0
        );
	}

    public static final BooleanProperty CAN_BEAR_FRUIT = BooleanProperty.create("can_bear_fruit");
    public static final IntegerProperty FRUIT_COOLDOWN = IntegerProperty.create("fruit_cooldown", 0, 100);
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CAN_BEAR_FRUIT);
        builder.add(FRUIT_COOLDOWN);
    }
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // 只要不是距离太远且不是永久树叶，就启用随机刻
        return !state.getValue(PERSISTENT) && state.getValue(DISTANCE) < 4;
    }
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 1. 先执行原版树叶凋零逻辑
        if (this.decaying(state)) {
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
            return;
        }

        // ★★★ 2. 处理冷却时间（每次随机刻减少1） ★★★
        int cooldown = state.getValue(FRUIT_COOLDOWN);
        if (cooldown > 0) {
            level.setBlock(pos, state.setValue(FRUIT_COOLDOWN, cooldown - 1), 3);
            return;  // 冷却期间跳过果实生成
        }

        // 3. 检查是否可以结果
        if (!state.getValue(CAN_BEAR_FRUIT)) {
            return;
        }

        // 4. 检查光照条件
        if (level.getMaxLocalRawBrightness(pos.above()) < 9) {
            return;
        }

        // 5. 检查下方是否有果实（3x3范围）
        if (hasFruitNearby(level, pos)) {
            return;
        }

        // 6. 随机概率生成果实
        if (random.nextInt(50) == 0) {
            spawnFruit(level, pos, random);
        }
    }
    private boolean hasFruitNearby(ServerLevel level, BlockPos pos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos checkPos = pos.offset(dx, -1, dz);
                BlockState checkState = level.getBlockState(checkPos);

                if (checkState.getBlock() instanceof DebrisApple ||
                        checkState.getBlock() instanceof SteelDebrisApple ||
                        checkState.getBlock() instanceof GoldenDebrisApple ||
                        checkState.getBlock() instanceof DiamondDebrisApple ||
                        checkState.getBlock() instanceof AncientDebrisApple) {
                    return true;
                }
            }
        }
        return false;
    }

    // ========== 生成果实 ==========
    private void spawnFruit(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos belowPos = pos.below();

        // 检查下方是否为空
        if (!level.getBlockState(belowPos).isAir()) {
            return;
        }

        // 根据概率选择果实
        Block fruitBlock = selectFruit(random);
        if (fruitBlock == null) {
            return;
        }

        // 生成果实（悬挂状态）
        BlockState fruitState = fruitBlock.defaultBlockState()
                .setValue(BlockStateProperties.HANGING, true)
                .setValue(BlockStateProperties.WATERLOGGED, false);

        level.setBlock(belowPos, fruitState, 3);

        // 播放音效（可选）
        level.levelEvent(2001, belowPos, Block.getId(fruitState));
    }
    public void onFruitPicked(Level level, BlockPos leavesPos) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(leavesPos);
            if (state.getBlock() instanceof DebrisLeavesBlock) {
                // 设置冷却为最大值100（约5-10秒）
                level.setBlock(leavesPos, state.setValue(FRUIT_COOLDOWN, 100), 3);
            }
        }
    }
    // ========== 根据概率选择果实 ==========
    private Block selectFruit(RandomSource random) {
        int roll = random.nextInt(100);  // 0-99

        if (roll < 80) {  // 80%
            return MutModBlocks.DEBRIS_APPLE.get();
        } else if (roll < 88) {  // 8%
            return MutModBlocks.STEEL_DEBRIS_APPLE.get();
        } else if (roll < 96) {  // 8%
            return MutModBlocks.GOLDEN_DEBRIS_APPLE.get();
        } else if (roll < 99) {  // 3%
            return MutModBlocks.DIAMOND_DEBRIS_APPLE.get();
        } else {  // 1%
            return MutModBlocks.ANCIENT_DEBRIS_APPLE.get();
        }
    }
}