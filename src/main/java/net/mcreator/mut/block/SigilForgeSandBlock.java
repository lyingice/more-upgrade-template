package net.mcreator.mut.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FallingBlock;

import com.mojang.serialization.MapCodec;

public class SigilForgeSandBlock extends FallingBlock {
	public static final MapCodec<SigilForgeSandBlock> CODEC = simpleCodec(properties -> new SigilForgeSandBlock());

	public MapCodec<SigilForgeSandBlock> codec() {
		return CODEC;
	}

	public SigilForgeSandBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(1f, 10f));
	}
}