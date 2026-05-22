package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.ButtonBlock;

public class DebrisPlanksButtonBlock extends ButtonBlock {
	public DebrisPlanksButtonBlock() {
		super(BlockSetType.OAK, 30, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).instrument(NoteBlockInstrument.BASS));
	}
}