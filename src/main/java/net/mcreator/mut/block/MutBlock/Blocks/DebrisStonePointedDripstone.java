package net.mcreator.mut.block.MutBlock.Blocks;

import com.mojang.serialization.MapCodec;
import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class DebrisStonePointedDripstone extends PointedDripstoneBlock {

    private static final int MAX_GROWTH_LENGTH = 7;
    private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
    private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;

    // ========================================
    // 自定义形状常量（与原版相同）
    // ========================================
    private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    public DebrisStonePointedDripstone() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.POINTED_DRIPSTONE)
                .strength(1.5f, 3.0f)
                .requiresCorrectToolForDrops()
                .randomTicks());
    }

    @Override
    public MapCodec<PointedDripstoneBlock> codec() {
        return MapCodec.unit(DebrisStonePointedDripstone::new);
    }

    // ========================================
    // ★ 核心修复：覆盖 getStateForPlacement
    // 原版 calculateTipDirection / calculateDripstoneThickness 都是 private static
    // 硬编码检查 Blocks.POINTED_DRIPSTONE，对我们返回 null/false
    // ========================================
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction lookDir = context.getNearestLookingVerticalDirection().getOpposite();

        Direction tipDir = calculateTipDirection(level, pos, lookDir);
        if (tipDir == null) return null;

        boolean sneak = context.isSecondaryUseActive(); // 原版：!isSecondaryUseActive() = 允许合并
        boolean allowMerge = !sneak;
        DripstoneThickness thickness = calculateDripstoneThickness(level, pos, tipDir, allowMerge);
        if (thickness == null) return null;

        return defaultBlockState()
                .setValue(TIP_DIRECTION, tipDir)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    // ========================================
    // ★ 覆盖 updateShape
    // 原版 calculateDripstoneThickness 是 private static
    // ========================================
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (direction != Direction.UP && direction != Direction.DOWN) {
            return state;
        }

        Direction tipDir = state.getValue(TIP_DIRECTION);
        if (direction == tipDir.getOpposite() && !canSurvive(state, level, pos)) {
            level.scheduleTick(pos, this, 2);
            return state;
        }

        boolean isMerge = state.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
        DripstoneThickness thickness = calculateDripstoneThickness(level, pos, tipDir, isMerge);
        return state.setValue(THICKNESS, thickness);
    }

    // ========================================
    // ★ 覆盖 tick
    // 原版调用 private static isStalagmite / spawnFallingStalactite
    // ========================================
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isStalagmite(state) && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        } else {
            spawnFallingStalactite(state, level, pos);
        }
    }

    // ========================================
    // ★ 覆盖 randomTick（生长逻辑）
    // 原版调用 private static 方法
    // ========================================
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < GROWTH_PROBABILITY_PER_RANDOM_TICK && isStalactiteStartPos(state, level, pos)) {
            // ★ 检查上方是否是残骸石 + 水
            BlockState above1 = level.getBlockState(pos.above(1));
            BlockState above2 = level.getBlockState(pos.above(2));
            if (above1.is(MutModBlocks.DEBRIS_STONE.get()) && above2.is(Blocks.WATER) && above2.getFluidState().isSource()) {
                growStalactiteOrStalagmiteIfPossible(state, level, pos, random);
            }
        }
    }

    // ========================================
    // ★ 覆盖 animateTick
    // 原版 canDrip → isStalactite（private）
    // ========================================
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (canDrip(state)) {
            float f = random.nextFloat();
            if (!(f > 0.12F)) {
                getFluidAboveStalactite(level, pos, state)
                        .filter(info -> f < 0.02F || canFillCauldron(info.fluid))
                        .ifPresent(info -> spawnDripParticle(level, pos, state, info.fluid));
            }
        }
    }

    // ========================================
    // ★ 覆盖 canSurvive — 已存在，但需要让 isValidPointedDripstonePlacement
    //    兼容我们的方块（已在父类中 override，但 updateShape 等直接调 private static 版本）
    // ========================================
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(TIP_DIRECTION);
        BlockPos supportPos = pos.relative(direction.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);

        // 我们的 isValidPointedDripstonePlacement 实现
        return isValidPointedDripstonePlacement(level, supportPos, direction, supportState);
    }

    // =====================
    // ★ 以下为自定义的"私有"辅助方法（替代原版 private static）
    // 全部使用 instanceof 检查而不是 Blocks.POINTED_DRIPSTONE
    // =====================

    /**
     * 检查 BlockState 是否是我们的滴水石锥，并且方向匹配
     */
    private static boolean isOurPointedDripstoneWithDirection(BlockState state, Direction direction) {
        return state.getBlock() instanceof DebrisStonePointedDripstone && state.getValue(TIP_DIRECTION) == direction;
    }

    /**
     * 检查是否是钟乳石（朝下）
     */
    private static boolean isStalactite(BlockState state) {
        return isOurPointedDripstoneWithDirection(state, Direction.DOWN);
    }

    /**
     * 检查是否是石笋（朝上）
     */
    private static boolean isStalagmite(BlockState state) {
        return isOurPointedDripstoneWithDirection(state, Direction.UP);
    }

    /**
     * 检查是否是钟乳石的起始位置（上方不是我们的滴水石锥）
     */
    private static boolean isStalactiteStartPos(BlockState state, LevelReader level, BlockPos pos) {
        return isStalactite(state) && !(level.getBlockState(pos.above()).getBlock() instanceof DebrisStonePointedDripstone);
    }

    /**
     * 计算放置方向
     */
    @Nullable
    private static Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction preferredDir) {
        Direction tipDir;
        if (isValidPointedDripstonePlacement(level, pos, preferredDir, level.getBlockState(pos.relative(preferredDir.getOpposite())))) {
            tipDir = preferredDir;
        } else {
            if (!isValidPointedDripstonePlacement(level, pos, preferredDir.getOpposite(), level.getBlockState(pos.relative(preferredDir)))) {
                return null;
            }
            tipDir = preferredDir.getOpposite();
        }
        return tipDir;
    }

    /**
     * 计算厚度（替代原版 private static calculateDripstoneThickness）
     */
    @Nullable
    private static DripstoneThickness calculateDripstoneThickness(LevelReader level, BlockPos pos, Direction tipDir, boolean allowMerge) {
        Direction opposite = tipDir.getOpposite();
        BlockState tipNeighbor = level.getBlockState(pos.relative(tipDir));

        // 如果尖端方向已有对面朝向的滴水石锥 → 尝试合并
        if (isOurPointedDripstoneWithDirection(tipNeighbor, opposite)) {
            return !allowMerge && tipNeighbor.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE
                    ? DripstoneThickness.TIP
                    : DripstoneThickness.TIP_MERGE;
        }

        // 如果尖端方向没有同向的滴水石锥 → 这是尖端
        if (!isOurPointedDripstoneWithDirection(tipNeighbor, tipDir)) {
            return DripstoneThickness.TIP;
        }

        // 有同向的滴水石锥在尖端方向
        DripstoneThickness neighborThickness = tipNeighbor.getValue(THICKNESS);
        if (neighborThickness == DripstoneThickness.TIP || neighborThickness == DripstoneThickness.TIP_MERGE) {
            return DripstoneThickness.FRUSTUM;
        }

        // 检查反方向
        BlockState baseNeighbor = level.getBlockState(pos.relative(opposite));
        if (!isOurPointedDripstoneWithDirection(baseNeighbor, tipDir)) {
            return DripstoneThickness.BASE;
        }

        return DripstoneThickness.MIDDLE;
    }

    /**
     * 检查是否能放置在这个位置（替代原版 private static isValidPointedDripstonePlacement）
     */
    private static boolean isValidPointedDripstonePlacement(LevelReader level, BlockPos pos, Direction direction, BlockState supportState) {
        BlockPos supportPos = pos.relative(direction.getOpposite());
        return supportState.isFaceSturdy(level, supportPos, direction)
                || isOurPointedDripstoneWithDirection(supportState, direction);
    }

    /**
     * 检查是否是尖端
     */
    private static boolean isTip(BlockState state, boolean includeMerge) {
        if (!(state.getBlock() instanceof DebrisStonePointedDripstone)) return false;
        DripstoneThickness thickness = state.getValue(THICKNESS);
        return thickness == DripstoneThickness.TIP || (includeMerge && thickness == DripstoneThickness.TIP_MERGE);
    }

    /**
     * 检查是否是未合并的尖端（仅 TIP，不含 TIP_MERGE）
     */
    private static boolean isUnmergedTipWithDirection(BlockState state, Direction direction) {
        return isTip(state, false) && state.getValue(TIP_DIRECTION) == direction;
    }

    /**
     * 检查是否能滴落流体
     */
    public static boolean canDrip(BlockState state) {
        return isStalactite(state) && state.getValue(THICKNESS) == DripstoneThickness.TIP && !state.getValue(WATERLOGGED);
    }

    /**
     * 找到尖端位置
     */
    @Nullable
    private static BlockPos findTip(BlockState state, LevelAccessor level, BlockPos pos, int maxDist, boolean includeMerge) {
        if (isTip(state, includeMerge)) return pos;

        Direction tipDir = state.getValue(TIP_DIRECTION);
        BiPredicate<BlockPos, BlockState> predicate = (p, s) ->
                s.getBlock() instanceof DebrisStonePointedDripstone && s.getValue(TIP_DIRECTION) == tipDir;

        return findBlockVertical(level, pos, tipDir.getAxisDirection(), predicate,
                        s -> isTip(s, includeMerge), maxDist)
                .orElse(null);
    }

    /**
     * 找到根方块（最顶端的滴水石锥或支撑方块）
     */
    private static Optional<BlockPos> findRootBlock(Level level, BlockPos pos, BlockState state, int maxDist) {
        Direction tipDir = state.getValue(TIP_DIRECTION);
        BiPredicate<BlockPos, BlockState> predicate = (p, s) ->
                s.getBlock() instanceof DebrisStonePointedDripstone && s.getValue(TIP_DIRECTION) == tipDir;

        return findBlockVertical(level, pos, tipDir.getOpposite().getAxisDirection(), predicate,
                        s -> !(s.getBlock() instanceof DebrisStonePointedDripstone), maxDist);
    }

    /**
     * 垂直方向查找方块
     */
    private static Optional<BlockPos> findBlockVertical(
            LevelAccessor level, BlockPos pos, Direction.AxisDirection axisDir,
            BiPredicate<BlockPos, BlockState> continuePred,
            Predicate<BlockState> foundPred, int maxDist) {
        Direction dir = Direction.get(axisDir, Direction.Axis.Y);
        BlockPos.MutableBlockPos mutable = pos.mutable();

        for (int i = 1; i < maxDist; i++) {
            mutable.move(dir);
            BlockState blockState = level.getBlockState(mutable);
            if (foundPred.test(blockState)) {
                return Optional.of(mutable.immutable());
            }
            if (level.isOutsideBuildHeight(mutable.getY()) || !continuePred.test(mutable, blockState)) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * 是否有足够的空间让流体滴落穿过非固体方块
     */
    private static boolean canDripThrough(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.isAir()) return true;
        if (state.isSolidRender(level, pos)) return false;
        if (!state.getFluidState().isEmpty()) return false;
        VoxelShape shape = state.getCollisionShape(level, pos);
        return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, shape, BooleanOp.AND);
    }

    /**
     * 生成掉落钟乳石
     */
    private static void spawnFallingStalactite(BlockState state, ServerLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState currentState = state;

        while (isStalactite(currentState)) {
            FallingBlockEntity falling = FallingBlockEntity.fall(level, mutable, currentState);
            if (isTip(currentState, true)) {
                int height = Math.max(1 + pos.getY() - mutable.getY(), 6);
                falling.setHurtsEntities(1.0F * height, 40);
                break;
            }
            mutable.move(Direction.DOWN);
            currentState = level.getBlockState(mutable);
        }
    }

    /**
     * 生长钟乳石或石笋
     */
    public static void growStalactiteOrStalagmiteIfPossible(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos tipPos = findTip(state, level, pos, MAX_GROWTH_LENGTH, false);
        if (tipPos != null) {
            BlockState tipState = level.getBlockState(tipPos);
            if (canDrip(tipState) && canTipGrow(tipState, level, tipPos)) {
                if (random.nextBoolean()) {
                    grow(level, tipPos, Direction.DOWN);
                } else {
                    growStalagmiteBelow(level, tipPos);
                }
            }
        }
    }

    /**
     * 检查尖端是否能继续生长
     */
    private static boolean canTipGrow(BlockState state, ServerLevel level, BlockPos pos) {
        Direction tipDir = state.getValue(TIP_DIRECTION);
        BlockPos forward = pos.relative(tipDir);
        BlockState forwardState = level.getBlockState(forward);
        if (!forwardState.getFluidState().isEmpty()) return false;
        return forwardState.isAir() || isUnmergedTipWithDirection(forwardState, tipDir.getOpposite());
    }

    /**
     * 在下方生长石笋
     */
    private static void growStalagmiteBelow(ServerLevel level, BlockPos tipPos) {
        BlockPos.MutableBlockPos mutable = tipPos.mutable();

        for (int i = 0; i < MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING; i++) {
            mutable.move(Direction.DOWN);
            BlockState blockState = level.getBlockState(mutable);
            if (!blockState.getFluidState().isEmpty()) return;

            if (isUnmergedTipWithDirection(blockState, Direction.UP) && canTipGrow(blockState, level, mutable)) {
                grow(level, mutable, Direction.UP);
                return;
            }

            if (isValidPointedDripstonePlacement(level, mutable, Direction.UP,
                    level.getBlockState(mutable.relative(Direction.DOWN)))
                    && !level.isWaterAt(mutable.below())) {
                grow(level, mutable.below(), Direction.UP);
                return;
            }

            if (!canDripThrough(level, mutable, blockState)) return;
        }
    }

    /**
     * 生长一格
     */
    private static void grow(ServerLevel level, BlockPos pos, Direction tipDir) {
        BlockPos forward = pos.relative(tipDir);
        BlockState forwardState = level.getBlockState(forward);
        if (isUnmergedTipWithDirection(forwardState, tipDir.getOpposite())) {
            createMergedTips(forwardState, level, forward);
        } else if (forwardState.isAir() || forwardState.is(Blocks.WATER)) {
            createDripstone(level, forward, tipDir, DripstoneThickness.TIP);
        }
    }

    /**
     * 创建合并的尖端
     */
    private static void createMergedTips(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockPos upper, lower;
        if (state.getValue(TIP_DIRECTION) == Direction.UP) {
            lower = pos;
            upper = pos.above();
        } else {
            upper = pos;
            lower = pos.below();
        }
        createDripstone(level, upper, Direction.DOWN, DripstoneThickness.TIP_MERGE);
        createDripstone(level, lower, Direction.UP, DripstoneThickness.TIP_MERGE);
    }

    /**
     * 创建我们的滴水石锥方块（使用自定义方块，非原版）
     */
    private static void createDripstone(LevelAccessor level, BlockPos pos, Direction tipDir, DripstoneThickness thickness) {
        BlockState state = net.mcreator.mut.init.MutModBlocks.DEBRIS_STONE_POINTED_DRIPSTONE.get()
                .defaultBlockState()
                .setValue(TIP_DIRECTION, tipDir)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        level.setBlock(pos, state, 3);
    }

    /**
     * 获取上方的流体信息
     */
    private static Optional<FluidInfo> getFluidAboveStalactite(Level level, BlockPos pos, BlockState state) {
        if (!isStalactite(state)) return Optional.empty();

        return findRootBlock(level, pos, state, 11).map(rootPos -> {
            BlockPos above = rootPos.above();
            BlockState aboveState = level.getBlockState(above);
            Fluid fluid;
            if (aboveState.is(Blocks.MUD) && !level.dimensionType().ultraWarm()) {
                fluid = Fluids.WATER;
            } else {
                fluid = level.getFluidState(above).getType();
            }
            return new FluidInfo(above, fluid, aboveState);
        });
    }

    /**
     * 检查是否能填满炼药锅
     */
    private static boolean canFillCauldron(Fluid fluid) {
        return fluid.getFluidType().getDripInfo() != null;
    }

    /**
     * 生成滴落粒子
     */
    private static void spawnDripParticle(Level level, BlockPos pos, BlockState state, Fluid fluid) {
        Vec3 offset = state.getOffset(level, pos);
        double x = pos.getX() + 0.5 + offset.x;
        double y = pos.getY() + 1 - 0.6875F - 0.0625;
        double z = pos.getZ() + 0.5 + offset.z;
        Fluid dripFluid = fluid.isSame(Fluids.EMPTY)
                ? (level.dimensionType().ultraWarm() ? Fluids.LAVA : Fluids.WATER)
                : fluid;
        ParticleOptions particle = dripFluid.getFluidType().getDripInfo() != null
                ? dripFluid.getFluidType().getDripInfo().dripParticle()
                : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
        if (particle != null) {
            level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    /**
     * 流体信息记录
     */
    private record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
    }

    // ========================================
    // 伤害相关（保留原逻辑，增加伤害倍率 12）
    // ========================================
    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceState,
                       FallingBlockEntity fallingEntity) {
        if (!level.isClientSide) {
            float damage = fallingEntity.fallDistance * 12.0F;
            if (damage > 0.0F) {
                for (Entity entity : level.getEntitiesOfClass(Entity.class,
                        fallingEntity.getBoundingBox().inflate(1.0))) {
                    if (!entity.isSpectator() && entity.isAlive()) {
                        entity.hurt(level.damageSources().fallingBlock(fallingEntity), damage);
                    }
                }
            }
        }
        super.onLand(level, pos, state, replaceState, fallingEntity);
    }
}
