package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DoorBlock;

public class DebrisStoneBricksDoorBlock extends DoorBlock {
	public DebrisStoneBricksDoorBlock() {
		super(BlockSetType.STONE, BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).instrument(NoteBlockInstrument.BASEDRUM));
	}
}