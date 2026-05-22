package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.SoundType;

import net.mcreator.mut.init.MutModWoodTypes;

public class DebrisPlanksSignBlock extends StandingSignBlock {
	public DebrisPlanksSignBlock() {
		super(MutModWoodTypes.DEBRIS_PLANKS_SIGN_WOOD_TYPE, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).noCollission().instrument(NoteBlockInstrument.BASS).forceSolidOn());
	}
}