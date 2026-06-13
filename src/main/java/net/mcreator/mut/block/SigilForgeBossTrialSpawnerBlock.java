package net.mcreator.mut.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.mcreator.mut.block.entity.SigilForgeBossTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.boss_spawner.BossSpawnerState;
import net.mcreator.mut.init.MutModBlockEntities;

import javax.annotation.Nullable;

public class SigilForgeBossTrialSpawnerBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BossSpawnerState> STATE = EnumProperty.create("boss_spawner_state", BossSpawnerState.class);

    public SigilForgeBossTrialSpawnerBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.TRIAL_SPAWNER)
                .strength(-1, 3600000)
                .lightLevel(s -> s.getValue(STATE) == BossSpawnerState.ACTIVE ? 15 : 8)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false)
                .isViewBlocking((bs, br, bp) -> false));  // ← 加这行，允许看到内部
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STATE, BossSpawnerState.INACTIVE));
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(SigilForgeBossTrialSpawnerBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    // ===== 方块实体 =====

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SigilForgeBossTrialSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, MutModBlockEntities.SIGIL_FORGE_BOSS_TRIAL_SPAWNER.get(),
                (pLevel, pPos, pState, pEntity) -> {
                    if (pEntity instanceof SigilForgeBossTrialSpawnerBlockEntity spawner) {
                        spawner.serverTick((ServerLevel) pLevel, pPos, pState);
                    }
                });
    }

    // ===== 交互 =====

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemInteractionResult.CONSUME;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SigilForgeBossTrialSpawnerBlockEntity spawnerEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 创造模式用刷怪蛋设置生物 - 放在最前面，优先处理
        if (stack.getItem() instanceof SpawnEggItem spawnEgg) {
            if (player.isCreative()) {
                EntityType<?> entityType = spawnEgg.getType(stack);
                spawnerEntity.setBossType(entityType);
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Boss set to: " + entityType.getDescription().getString()),
                        true);
                return ItemInteractionResult.SUCCESS;
            } else {
                // 生存模式不能设置刷怪蛋
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }

        // 用钥匙激活
        if (spawnerEntity.tryActivate(serverLevel, pos, player, stack)) {
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}