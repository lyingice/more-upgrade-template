package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.CeilingHangingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;

public class DebrisStoneHangingSignBlock extends CeilingHangingSignBlock {
	public DebrisStoneHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_STONE_HANGING_SIGN_WOOD_TYPE, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2f, 10f).requiresCorrectToolForDrops().noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn());
	}
}