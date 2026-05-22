package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.StandingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;

public class DebrisCobblestoneSignBlock extends StandingSignBlock {
	public DebrisCobblestoneSignBlock() {
		super(MutModWoodTypes.DEBRIS_COBBLESTONE_SIGN_WOOD_TYPE, BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}