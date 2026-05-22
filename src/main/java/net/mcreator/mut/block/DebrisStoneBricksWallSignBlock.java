package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.SoundType;

import net.mcreator.mut.init.MutModWoodTypes;
import net.mcreator.mut.init.MutModBlocks;

public class DebrisStoneBricksWallSignBlock extends WallSignBlock {
	public DebrisStoneBricksWallSignBlock() {
		super(MutModWoodTypes.DEBRIS_STONE_BRICKS_SIGN_WOOD_TYPE,
				BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().dropsLike(MutModBlocks.DEBRIS_STONE_BRICKS_SIGN.get()));
	}
}