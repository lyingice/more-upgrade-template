package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallBlock;

public class DebrisCobblestoneWallBlock extends WallBlock {
	public DebrisCobblestoneWallBlock() {
		super(BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}