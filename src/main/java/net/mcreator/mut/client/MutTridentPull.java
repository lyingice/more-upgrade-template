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
                    MutModItems.ADVANCED_STEEL_TRIDENT.get(),
                    MutModItems.OBSIDIAN_TRIDENT.get(),
                    MutModItems.NETHERITE_OBSIDIAN_TRIDENT.get(),
                    MutModItems.CRYING_OBSIDIAN_TRIDENT.get(),
                    MutModItems.NETHER_STAR_TRIDENT.get(),
                    MutModItems.DRAGON_TRIDENT.get(),
                    MutModItems.WITHER_TRIDENT.get(),
                    MutModItems.NETHERITE_COPPER_TRIDENT.get(),
                    MutModItems.NETHERITE_EMERALD_TRIDENT.get(),
                    MutModItems.NETHERITE_REDSTONE_TRIDENT.get(),
                    MutModItems.NETHERITE_AMETHYST_TRIDENT.get(),
                    MutModItems.AMETHYST_TRIDENT.get(),
                    MutModItems.EMERALD_TRIDENT.get(),
                    MutModItems.LAPIS_LAZULI_TRIDENT.get(),
                    MutModItems.NETHERITE_LAPIS_LAZULI_TRIDENT.get(),
                    MutModItems.ECHOITE_TRIDENT.get(),
                    MutModItems.POISON_STEEL_TRIDENT.get(),
                    MutModItems.FLAME_GOLD_TRIDENT.get(),
                    MutModItems.THUNDER_COPPER_TRIDENT.get(),
                    MutModItems.UNCANNY_AMETHYST_TRIDENT.get()

            );

            for (Item item : tridents) {
                ItemProperties.register(item,
                        ResourceLocation.parse("throwing"),
                        (stack, level, entity, seed) -> {
                            if (entity == null) return 0.0F;
                            return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                        });
            }
        });
    }
}