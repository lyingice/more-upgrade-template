package net.mcreator.mut.block.MutBlock.Blocks;

import com.mojang.serialization.MapCodec;
import net.mcreator.mut.block.DebrisLeavesBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DebrisAppleBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final VoxelShape standingShape;
    private final VoxelShape hangingShape;

/**
 * 构造函数，创建一个DebrisAppleBlock实例
 * @param standingShape 站立状态的方块碰撞体积形状
 * @param hangingShape 悬挂状态的方块碰撞体积形状
 */
    public DebrisAppleBlock(VoxelShape standingShape, VoxelShape hangingShape) {
    // 调用父类构造函数，设置方块基本属性
        super(Properties.of()
                .sound(SoundType.CROP)    // 设置方块声音为作物声音
                .strength(0.5f)          // 设置方块硬度为0.5
                .noOcclusion());         // 设置方块不遮挡光线
    // 初始化站立状态和悬挂状态的碰撞体积形状
        this.standingShape = standingShape;
        this.hangingShape = hangingShape;
    // 注册默认方块状态，包括是否悬挂和是否含水
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HANGING, false)    // 默认不悬挂
                .setValue(WATERLOGGED, false)); // 默认不含水
    }

    @Override  // 重写父类的方法，表示这是一个对父类方法的覆盖实现
    protected MapCodec<? extends Block> codec() {  // 定义一个受保护的方法，返回一个MapCodec类型的对象，该对象可以编码/解码Block的子类
        return MapCodec.unit(() -> new DebrisAppleBlock(standingShape, hangingShape));  // 返回一个MapCodec实例，它使用unit方法创建一个固定值，该值为一个新的DebrisAppleBlock实例，使用standingShape和hangingShape作为参数
    }

    @Override    // 重写父类方法，用于定义方块状态
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // 添加方块的两个状态属性：是否悬挂(HANGING)和是否含水(WATERLOGGED)
        builder.add(HANGING, WATERLOGGED);
    }

/**
 * 获取方块在放置时的状态
 * @param context 方块放置的上下文信息，包含放置位置、方向等
 * @return 返回方块的状态，如果无法放置则返回null
 */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
    // 获取点击位置周围的流体状态
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
    // 遍历最近的观察方向
        for (Direction direction : context.getNearestLookingDirections()) {
        // 检查方向是否为垂直方向（Y轴）
            if (direction.getAxis() == Direction.Axis.Y) {
            // 设置方块状态，根据方向决定是否悬挂
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
            // 检查方块是否能在指定位置存活
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                // 设置方块是否被水包围的状态，取决于当前位置是否为水
                    return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                }
            }
        }
    // 如果无法放置，返回null
        return null;
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(HANGING) ? hangingShape : standingShape;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        BlockPos supportPos = pos.relative(direction);
        BlockState supportState = level.getBlockState(supportPos);

        // 检查支撑方块是否是树叶（原版或自定义）
        return supportState.getBlock() instanceof LeavesBlock ||
                supportState.getBlock() instanceof DebrisLeavesBlock ||
                // 也支持原版树叶
                supportState.is(BlockTags.LEAVES);
    }

    protected static Direction getConnectedDirection(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        // 在果实被破坏时触发冷却
        if (!level.isClientSide && state.getBlock() instanceof DebrisAppleBlock && newState.isAir()) {
            // 掉落果实（如果是玩家破坏，已经在 playerDestroy 中处理了）
            // 但这里需要触发树叶冷却
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.getBlock() instanceof DebrisLeavesBlock leaves) {
                leaves.onFruitPicked(level, abovePos);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
