package net.mcreator.mut.block;

import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Heterochromaticamethyst2Block extends AmethystClusterBlock {

    public Heterochromaticamethyst2Block() {
        super(5.0f, 3.0f, BlockBehaviour.Properties.of()
                .sound(SoundType.MEDIUM_AMETHYST_BUD)
                .strength(5f, 10f)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false));
    }
}