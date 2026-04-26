package net.mcreator.mut.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class BlueDiamondDebrisBlock extends Block {
	public BlueDiamondDebrisBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.ANCIENT_DEBRIS).strength(30f, 1200f).requiresCorrectToolForDrops().pushReaction(PushReaction.IGNORE));
	}
}