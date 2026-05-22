package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.TrapDoorBlock;

public class DebrisCobblestoneTrapDoorBlock extends TrapDoorBlock {
	public DebrisCobblestoneTrapDoorBlock() {
		super(BlockSetType.OAK, BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false).instrument(NoteBlockInstrument.BASEDRUM));
	}
}