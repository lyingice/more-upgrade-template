package net.mcreator.mut.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class NetheriteRedstoneBlockBlock extends Block {
	public NetheriteRedstoneBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).sound(SoundType.NETHERITE_BLOCK).strength(50f, 1200f).requiresCorrectToolForDrops());
	}
}