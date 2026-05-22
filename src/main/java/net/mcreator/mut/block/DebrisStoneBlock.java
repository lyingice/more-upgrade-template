package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

public class DebrisStoneBlock extends Block {
	public DebrisStoneBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2f, 10f).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM));
	}
}