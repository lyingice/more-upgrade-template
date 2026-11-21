package net.mcreator.mut.porting.ruingolem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** 岩石傀儡石像的方块实体，仅用于渲染。 */
public class RockGolemBlockEntity extends BlockEntity {

    public RockGolemBlockEntity(BlockPos pos, BlockState state) {
        super(RuinGolemBlocks.ROCK_GOLEM_BLOCK_ENTITY.get(), pos, state);
    }
}
