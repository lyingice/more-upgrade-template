package net.mcreator.mut.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.item.SpearItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class MutItemProperties {

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        net.minecraft.core.registries.BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof SpearItem spear) {
                registerSpearProperties(spear);
            }
        });
    }

    private static void registerSpearProperties(SpearItem spear) {
        ItemProperties.register(
                spear,
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "throwing"),
                (stack, level, entity, seed) -> {
                    if (entity != null && entity.isUsingItem() && entity.getUseItem() == stack) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );

        ItemProperties.register(
                spear,
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "jerotes_charge_stage"),
                (stack, level, entity, seed) -> {
                    if (entity != null && entity.isUsingItem() && entity.getUseItem() == stack) {
                        int useTime = entity.getTicksUsingItem();
                        int delayTicks = spear.getDelayTicks();
                        int dismountEnd = spear.getDismountEndTick();
                        int knockbackEnd = spear.getKnockbackEndTick();
                        int damageEnd = spear.getDamageEndTick();

                        if (useTime < delayTicks) return 0.0F;
                        if (useTime < dismountEnd) return 1.0F;
                        if (useTime < knockbackEnd) return 2.0F;
                        if (useTime < damageEnd) return 3.0F;
                        return 0.0F;
                    }
                    return 0.0F;
                }
        );

        ItemProperties.register(
                spear,
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "jerotes_swing"),
                (stack, level, entity, seed) -> {
                    if (entity != null && entity.swinging && entity.getMainHandItem() == stack) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );

        ItemProperties.register(
                spear,
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "jerotes_shift"),
                (stack, level, entity, seed) -> {
                    if (entity != null && entity.isShiftKeyDown()) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );
    }
}