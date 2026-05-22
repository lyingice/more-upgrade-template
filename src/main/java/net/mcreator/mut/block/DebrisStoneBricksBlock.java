package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class DebrisStoneBricksBlock extends Block {
	public DebrisStoneBricksBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).instrument(NoteBlockInstrument.BASEDRUM));
	}
}