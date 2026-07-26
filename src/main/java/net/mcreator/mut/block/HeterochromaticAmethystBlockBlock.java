package net.mcreator.mut.block;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class HeterochromaticAmethystBlockBlock extends Block {
	public HeterochromaticAmethystBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.AMETHYST).strength(10f).lightLevel(s -> 15).requiresCorrectToolForDrops().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true));
	}
}