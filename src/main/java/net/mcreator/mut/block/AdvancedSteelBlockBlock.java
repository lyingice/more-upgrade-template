package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class AdvancedSteelBlockBlock extends Block {
	public AdvancedSteelBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.NETHERITE_BLOCK).strength(50f, 1200f).requiresCorrectToolForDrops());
	}
}