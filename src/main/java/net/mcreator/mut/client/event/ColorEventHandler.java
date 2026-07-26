package net.mcreator.mut.client.event;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.mcreator.mut.init.MutModBlocks;

@EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
public class ColorEventHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
        // 草方块颜色
        event.register((bs, world, pos, index) -> {
            return world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D);
        }, MutModBlocks.SIGIL_FORGE_GRASS.get());

        // 树叶颜色
        event.register((bs, world, pos, index) -> {
            return world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
        }, MutModBlocks.DEBRIS_LEAVES.get());
    }
}