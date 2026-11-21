package net.mcreator.mut.porting.ruingolem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 岩石傀儡的石像方块。
 * <p>
 * 外观由 BlockEntity 渲染器画出，和活着的岩石傀儡是同一套模型与贴图；
 * 碰撞箱按傀儡本体尺寸（1.4 宽 × 2.7 高）摆放，所以它“看起来、撞起来”都像那只傀儡。
 */
public class RockGolemBlock extends BaseEntityBlock {

    /** 傀儡本体：宽 1.4，高 2.7，以方块中心为原点 */
    private static final VoxelShape SHAPE =
            Shapes.box(-0.2D, 0.0D, -0.2D, 1.2D, 2.7D, 1.2D);

    public static final MapCodec<RockGolemBlock> CODEC = simpleCodec(RockGolemBlock::new);

    public RockGolemBlock() {
        this(BlockBehaviour.Properties.of()
                .strength(3.5F, 1200.0F)
                .noOcclusion()
                .dynamicShape());
    }

    public RockGolemBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RockGolemBlockEntity(pos, state);
    }
}
