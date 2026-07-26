package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class NetheriteLapisLazuliBlockBlock extends Block {
	public NetheriteLapisLazuliBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.LAPIS).sound(SoundType.NETHERITE_BLOCK).strength(50f, 1200f).requiresCorrectToolForDrops());
	}
}