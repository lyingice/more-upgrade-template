package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.CeilingHangingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;

public class DebrisStoneBricksHangingSignBlock extends CeilingHangingSignBlock {
	public DebrisStoneBricksHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_STONE_BRICKS_HANGING_SIGN_WOOD_TYPE, BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}