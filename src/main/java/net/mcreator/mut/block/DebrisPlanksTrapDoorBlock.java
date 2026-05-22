package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.SoundType;

public class DebrisPlanksTrapDoorBlock extends TrapDoorBlock {
	public DebrisPlanksTrapDoorBlock() {
		super(BlockSetType.OAK, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(4f, 100f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).instrument(NoteBlockInstrument.BASS));
	}
}