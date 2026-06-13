package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

public class DeepIronRawBlockBlock extends Block {
	public DeepIronRawBlockBlock() {
		super(BlockBehaviour.Properties.of().strength(10f, 200f).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM));
	}
}