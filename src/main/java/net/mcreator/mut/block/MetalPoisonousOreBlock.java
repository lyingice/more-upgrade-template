package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

public class MetalPoisonousOreBlock extends Block {
	public MetalPoisonousOreBlock() {
		super(BlockBehaviour.Properties.of().strength(4f, 20f).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM));
	}
}