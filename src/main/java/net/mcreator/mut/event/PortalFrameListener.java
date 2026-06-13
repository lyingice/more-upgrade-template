package net.mcreator.mut.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.mcreator.mut.procedures.PortalActivateByLavaProcedure;

@EventBusSubscriber
public class PortalFrameListener {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() != Blocks.SMITHING_TABLE) {
            return;
        }

        boolean success = PortalActivateByLavaProcedure.execute(
                event.getLevel(),
                event.getPos().getX(),
                event.getPos().getY(),
                event.getPos().getZ(),
                event.getFace(),
                event.getItemStack(),
                event.getEntity()
        );

        if (success) {
            event.setCanceled(true);  // 取消岩浆放置
        }
    }

}