package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.SoundType;

public class DebrisStoneBricksWallBlock extends WallBlock {
	public DebrisStoneBricksWallBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}