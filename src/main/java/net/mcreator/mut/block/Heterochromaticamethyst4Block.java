package net.mcreator.mut.block;

import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Heterochromaticamethyst4Block extends AmethystClusterBlock {

    public Heterochromaticamethyst4Block() {
        super(11.0f, 3.0f, BlockBehaviour.Properties.of()
                .sound(SoundType.MEDIUM_AMETHYST_BUD)
                .strength(5f, 10f)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false));
    }
}