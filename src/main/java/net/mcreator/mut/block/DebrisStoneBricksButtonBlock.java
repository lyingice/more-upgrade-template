package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.ButtonBlock;

public class DebrisStoneBricksButtonBlock extends ButtonBlock {
	public DebrisStoneBricksButtonBlock() {
		super(BlockSetType.STONE, 20, BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(8f, 200f).instrument(NoteBlockInstrument.BASEDRUM));
	}
}