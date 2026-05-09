package net.mcreator.mut.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.mcreator.mut.init.MutModItems;

import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
public class MutTridentPull {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            List<Item> tridents = Arrays.asList(
                    MutModItems.WOODEN_TRIDENT.get(),
                    MutModItems.COPPER_TRIDENT.get(),
                    MutModItems.IRON_TRIDENT.get(),
                    MutModItems.GOLDEN_TRIDENT.get(),
                    MutModItems.DIAMOND_TRIDENT.get(),
                    MutModItems.NETHERITE_TRIDENT.get(),
                    MutModItems.STEEL_TRIDENT.get(),
                    MutModItems.GILDING_TRIDENT.get(),
                    MutModItems.BLUE_DIAMOND_TRIDENT.get(),
                    MutModItems.ADVANCED_STEEL_TRIDENT.get()
                    //MutModItems._TRIDENT.get()
            );

            for (Item item : tridents) {
                ItemProperties.register(item,
                        ResourceLocation.parse("throwing"),
                        (stack, level, entity, seed) -> {
                            if (entity == null) return 0.0F;
                            return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                        });

                System.out.println("Registered trident properties for: " + BuiltInRegistries.ITEM.getKey(item));
            }
        });
    }
}