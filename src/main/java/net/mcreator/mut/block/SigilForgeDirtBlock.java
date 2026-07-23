package net.mcreator.mut.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;

public class SigilForgeDirtBlock extends SnowyDirtBlock {

    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    public SigilForgeDirtBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.GRAVEL)
                .strength(0.5f, 10f)
                .randomTicks()  // 需要随机刻来支持被草方块扩散
        );
        // 注册默认状态，默认 SNOWY = false
        this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, false));
    }

    // ========== 1. 铲子右键转化为普通泥土 ==========
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hitResult) {
        ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        // 检查是否手持铲子
        if (handItem.getItem() == Items.WOODEN_SHOVEL ||
                handItem.getItem() == Items.STONE_SHOVEL ||
                handItem.getItem() == Items.IRON_SHOVEL ||
                handItem.getItem() == Items.GOLDEN_SHOVEL ||
                handItem.getItem() == Items.DIAMOND_SHOVEL ||
                handItem.getItem() == Items.NETHERITE_SHOVEL) {

            if (!level.isClientSide) {
                // 将符锻韧土转化为普通泥土
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);

                // 消耗铲子耐久
                handItem.hurtAndBreak(1, player, player.getSlotForHand(InteractionHand.MAIN_HAND));

                // 播放音效
                level.levelEvent(player, 2001, pos, Block.getId(Blocks.DIRT.defaultBlockState()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    // ========== 2. 随机刻：空实现，让草方块来控制扩散 ==========
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 不执行任何操作
        // 草方块的扩散逻辑会主动将本方块转化为草方块
        // 本方法保留是为了让草方块能够通过 scheduleTick 等方式触发
    }

    // ========== 3. 更新形状：当上方方块变化时更新 SNOWY 属性 ==========
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // 当上方方块变化时，更新 SNOWY 属性
        if (direction == Direction.UP) {
            return state.setValue(SNOWY, isSnowySetting(neighborState));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    // ========== 4. 放置时设置 SNOWY 属性 ==========
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState aboveState = context.getLevel().getBlockState(context.getClickedPos().above());
        return this.defaultBlockState().setValue(SNOWY, isSnowySetting(aboveState));
    }

    // ========== 5. 判断上方是否为雪 ==========
    private static boolean isSnowySetting(BlockState state) {
        return state.is(BlockTags.SNOW);
    }

    // ========== 6. 注册状态属性 ==========
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }

    // ========== 7. 检查是否可以存活（总是可以） ==========
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    // ========== 8. 破坏时掉落自身 ==========
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide && !player.isCreative()) {
            Block.popResource(level, pos, new ItemStack(this));
        }
    }
}