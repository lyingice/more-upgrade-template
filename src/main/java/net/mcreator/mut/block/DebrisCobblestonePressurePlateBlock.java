package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.PressurePlateBlock;

public class DebrisCobblestonePressurePlateBlock extends PressurePlateBlock {
	public DebrisCobblestonePressurePlateBlock() {
		super(BlockSetType.STONE, BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}