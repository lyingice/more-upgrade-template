package net.mcreator.mut.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
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

import net.mcreator.mut.block.entity.SigilForgeTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerState;
import net.mcreator.mut.init.MutModBlockEntities;

import javax.annotation.Nullable;

public class SigilForgeTrialSpawnerBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<TrialSpawnerState> STATE = EnumProperty.create("trial_spawner_state", TrialSpawnerState.class);

    public SigilForgeTrialSpawnerBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.TRIAL_SPAWNER)
                .strength(-1, 3600000)
                .lightLevel(s -> s.getValue(STATE) == TrialSpawnerState.ACTIVE ? 12 : 6)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STATE, TrialSpawnerState.INACTIVE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(SigilForgeTrialSpawnerBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SigilForgeTrialSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, MutModBlockEntities.SIGIL_FORGE_TRIAL_SPAWNER.get(),
                (pLevel, pPos, pState, pEntity) -> {
                    if (pEntity instanceof SigilForgeTrialSpawnerBlockEntity spawner) {
                        spawner.serverTick((ServerLevel) pLevel, pPos, pState);
                    }
                });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isCreative() && stack.getItem() instanceof SpawnEggItem) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SigilForgeTrialSpawnerBlockEntity spawner) {
                if (spawner.trySetMobType(player, stack)) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}