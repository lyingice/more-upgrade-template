package net.mcreator.mut.block.MutBlock.Blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;

public class DebrisStonePointedDripstone extends PointedDripstoneBlock {

    public DebrisStonePointedDripstone() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.POINTED_DRIPSTONE)
                .strength(1.5f, 3.0f)
                .requiresCorrectToolForDrops()
                .randomTicks());
    }

    @Override
    public MapCodec<PointedDripstoneBlock> codec() {
        return MapCodec.unit(DebrisStonePointedDripstone::new);
    }
}