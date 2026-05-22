package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallHangingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;
import net.mcreator.mut.init.MutModBlocks;

public class DebrisCobblestoneWallHangingSignBlock extends WallHangingSignBlock {
	public DebrisCobblestoneWallHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_COBBLESTONE_HANGING_SIGN_WOOD_TYPE,
				BlockBehaviour.Properties.of().strength(2f, 10f).requiresCorrectToolForDrops().noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().dropsLike(MutModBlocks.DEBRIS_COBBLESTONE_HANGING_SIGN.get()));
	}
}