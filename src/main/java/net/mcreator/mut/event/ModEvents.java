package net.mcreator.mut.event;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = MutMod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    MutModBlocks.IRON_FLOWER.getId(),
                    MutModBlocks.POTTED_IRON_FLOWER
            );
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    MutModBlocks.BLUE_DIAMOND_ROSE.getId(),
                    MutModBlocks.POTTED_BLUE_DIAMOND_ROSE
            );
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    MutModBlocks.GILDED_MARIGOLD_FLOWER.getId(),
                    MutModBlocks.POTTED_GILDED_MARIGOLD_FLOWER
            );
        });
    }
}
