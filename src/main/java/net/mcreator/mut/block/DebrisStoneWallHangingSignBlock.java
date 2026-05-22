package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.WallHangingSignBlock;

import net.mcreator.mut.init.MutModWoodTypes;
import net.mcreator.mut.init.MutModBlocks;

public class DebrisStoneWallHangingSignBlock extends WallHangingSignBlock {
	public DebrisStoneWallHangingSignBlock() {
		super(MutModWoodTypes.DEBRIS_STONE_HANGING_SIGN_WOOD_TYPE,
				BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2f, 10f).requiresCorrectToolForDrops().noCollission().instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().dropsLike(MutModBlocks.DEBRIS_STONE_HANGING_SIGN.get()));
	}
}