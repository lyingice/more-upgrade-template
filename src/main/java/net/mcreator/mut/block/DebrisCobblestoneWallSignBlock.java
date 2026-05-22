package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;
import net.mcreator.mut.init.MutModBlocks;

public class DebrisCobblestoneWallSignBlock extends WallSignBlock {
	public DebrisCobblestoneWallSignBlock() {
		super(MutModWoodTypes.DEBRIS_COBBLESTONE_SIGN_WOOD_TYPE,
				BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().dropsLike(MutModBlocks.DEBRIS_COBBLESTONE_SIGN.get()));
	}
}