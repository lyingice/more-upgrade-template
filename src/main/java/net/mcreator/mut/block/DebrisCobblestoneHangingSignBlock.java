package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.CeilingHangingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;

public class DebrisCobblestoneHangingSignBlock extends CeilingHangingSignBlock {
	public DebrisCobblestoneHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_COBBLESTONE_HANGING_SIGN_WOOD_TYPE, BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}