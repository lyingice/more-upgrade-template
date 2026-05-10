package net.mcreator.mut.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.procedures.GetPullingProcedure;
import net.mcreator.mut.procedures.MutBowGetPullProcedure;

import java.util.Arrays;
import java.util.List;
import net.minecraft.world.item.Item;

@EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
public class MutBowPull {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientLoad(FMLClientSetupEvent event) {
        System.out.println("========== MutBowPull LOADED! ==========");
        event.enqueueWork(() -> {
            System.out.println("========== MutBowPull enqueueWork RUNNING! ==========");

            // 直接列出所有弓，不依赖标签
            List<Item> bows = Arrays.asList(
                    MutModItems.IRON_BOW.get(),
                    MutModItems.DIAMOND_BOW.get(),
                    MutModItems.GOLDEN_BOW.get(),
                    MutModItems.NETHERITE_BOW.get(),
                    MutModItems.STEEL_BOW.get(),
                    MutModItems.GILDING_BOW.get(),
                    MutModItems.ADVANCED_STEEL_BOW.get(),
                    MutModItems.NETHER_STAR_BOW.get(),
                    MutModItems.BLUE_DIAMOND_BOW.get(),
                    MutModItems.OBSIDIAN_BOW.get(),
                    MutModItems.NETHERITE_OBSIDIAN_BOW.get(),
                    MutModItems.CRYING_OBSIDIAN_BOW.get(),
                    MutModItems.COPPER_BOW.get(),
                    MutModItems.NETHERITE_COPPER_BOW.get(),
                    MutModItems.DRAGON_BOW.get()
            );

            for (Item item : bows) {
                String path = BuiltInRegistries.ITEM.getKey(item).getPath();
                System.out.println("Registering properties for: " + path);

                // 注册 pulling 属性
                ItemProperties.register(item,
                        ResourceLocation.parse("minecraft:pulling"),
                        (itemStackToRender, clientWorld, entity, itemEntityId) -> (float) GetPullingProcedure.execute(entity, itemStackToRender));

                // 注册 pull 属性
                ItemProperties.register(item,
                        ResourceLocation.parse("minecraft:pull"),
                        (itemStackToRender, clientWorld, entity, itemEntityId) -> (float) MutBowGetPullProcedure.execute(entity, itemStackToRender));
            }
        });
    }
}