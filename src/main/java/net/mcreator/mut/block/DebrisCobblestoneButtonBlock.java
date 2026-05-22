package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.ButtonBlock;

public class DebrisCobblestoneButtonBlock extends ButtonBlock {
	public DebrisCobblestoneButtonBlock() {
		super(BlockSetType.STONE, 20, BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM));
	}
}