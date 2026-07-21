package net.mcreator.mut.client;

import net.mcreator.mut.MutMod;
import net.minecraft.spearcore.client.animation.SpearAnimations;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
@Deprecated
public class SpearChargeHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 命中反馈动画倒计时
        if (SpearAnimations.spearHitTicks > 0) {
            SpearAnimations.spearHitTicks--;
        }
    }
}
