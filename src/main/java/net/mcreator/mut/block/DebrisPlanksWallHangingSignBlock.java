package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.SoundType;

import net.mcreator.mut.init.MutModWoodTypes;
import net.mcreator.mut.init.MutModBlocks;

public class DebrisPlanksWallHangingSignBlock extends WallHangingSignBlock {
	public DebrisPlanksWallHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_PLANKS_HANGING_SIGN_WOOD_TYPE,
				BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).noCollission().instrument(NoteBlockInstrument.BASS).forceSolidOn().dropsLike(MutModBlocks.DEBRIS_PLANKS_HANGING_SIGN.get()));
	}
}