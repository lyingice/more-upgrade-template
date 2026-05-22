package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.CeilingHangingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;

public class DebrisPlanksHangingSignBlock extends CeilingHangingSignBlock {
	public DebrisPlanksHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_PLANKS_HANGING_SIGN_WOOD_TYPE, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).noCollission().instrument(NoteBlockInstrument.BASS).forceSolidOn());
	}
}