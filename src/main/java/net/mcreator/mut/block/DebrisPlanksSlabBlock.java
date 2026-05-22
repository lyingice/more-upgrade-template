package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SlabBlock;

public class DebrisPlanksSlabBlock extends SlabBlock {
	public DebrisPlanksSlabBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).instrument(NoteBlockInstrument.BASS));
	}
}