package net.mcreator.mut.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.mcreator.mut.init.MutModItems;

import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
public class MutCrossbowPull {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            List<Item> crossbows = Arrays.asList(
                    MutModItems.COPPER_CROSSBOW.get(),
                    MutModItems.IRON_CROSSBOW.get(),
                    MutModItems.GOLDEN_CROSSBOW.get(),
                    MutModItems.DIAMOND_CROSSBOW.get(),
                    MutModItems.NETHERITE_CROSSBOW.get(),
                    MutModItems.STEEL_CROSSBOW.get(),
                    MutModItems.GILDING_CROSSBOW.get(),
                    MutModItems.BLUE_DIAMOND_CROSSBOW.get(),
                    MutModItems.ADVANCED_STEEL_CROSSBOW.get(),
                    MutModItems.OBSIDIAN_CROSSBOW.get(),
                    MutModItems.NETHERITE_OBSIDIAN_CROSSBOW.get(),
                    MutModItems.CRYING_OBSIDIAN_CROSSBOW.get(),
                    MutModItems.NETHER_STAR_CROSSBOW.get(),
                    MutModItems.WITHER_CROSSBOW.get(),
                    MutModItems.DRAGON_CROSSBOW.get(),
                    MutModItems.NETHERITE_COPPER_CROSSBOW.get(),
                    MutModItems.EMERALD_CROSSBOW.get(),
                    MutModItems.NETHERITE_EMERALD_CROSSBOW.get(),
                    MutModItems.NETHERITE_REDSTONE_CROSSBOW.get(),
                    MutModItems.AMETHYST_CROSSBOW.get(),
                    MutModItems.NETHERITE_AMETHYST_CROSSBOW.get()
            );

            for (Item item : crossbows) {
                // pulling
                ItemProperties.register(item,
                        ResourceLocation.parse("pulling"),
                        (stack, level, entity, seed) -> {
                            if (entity == null) return 0.0F;
                            return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                        });

                // pull
                ItemProperties.register(item,
                        ResourceLocation.parse("pull"),
                        (stack, level, entity, seed) -> {
                            if (entity == null) return 0.0F;
                            if (entity.getUseItem() != stack) return 0.0F;
                            int useDuration = stack.getUseDuration(entity);
                            int remaining = entity.getUseItemRemainingTicks();
                            if (useDuration <= 0) return 0.0F;
                            return (float)(useDuration - remaining) / (float)useDuration;
                        });

                // charged
                ItemProperties.register(item,
                        ResourceLocation.parse("charged"),
                        (stack, level, entity, seed) ->
                                CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

                // firework
                ItemProperties.register(item,
                        ResourceLocation.parse("firework"),
                        (stack, level, entity, seed) -> {
                            if (!CrossbowItem.isCharged(stack)) return 0.0F;
                            var charged = stack.get(net.minecraft.core.component.DataComponents.CHARGED_PROJECTILES);
                            if (charged == null || charged.isEmpty()) return 0.0F;
                            boolean isFirework = charged.getItems().stream()
                                    .anyMatch(s -> s.getItem() == net.minecraft.world.item.Items.FIREWORK_ROCKET);
                            return isFirework ? 1.0F : 0.0F;
                        });

                System.out.println("Registered crossbow properties for: " + BuiltInRegistries.ITEM.getKey(item));
            }
        });
    }
}