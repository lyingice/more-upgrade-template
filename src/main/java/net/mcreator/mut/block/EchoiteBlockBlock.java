package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class EchoiteBlockBlock extends Block {
	public EchoiteBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.NETHERITE_BLOCK).strength(50f, 1200f).requiresCorrectToolForDrops());
	}
}