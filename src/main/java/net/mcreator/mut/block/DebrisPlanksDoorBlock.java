package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DoorBlock;

public class DebrisPlanksDoorBlock extends DoorBlock {
	public DebrisPlanksDoorBlock() {
		super(BlockSetType.OAK, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).instrument(NoteBlockInstrument.BASS));
	}
}