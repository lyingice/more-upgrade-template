package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.SoundType;

public class DebrisStoneBricksTrapDoorBlock extends TrapDoorBlock {
	public DebrisStoneBricksTrapDoorBlock() {
		super(BlockSetType.STONE, BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).instrument(NoteBlockInstrument.BASEDRUM));
	}
}