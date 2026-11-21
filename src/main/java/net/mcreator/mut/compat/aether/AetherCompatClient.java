package net.mcreator.mut.compat.aether;

import com.aetherteam.aether.client.renderer.accessory.GlovesRenderer;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 天境护手的客户端渲染注册。
 * <p>
 * 单独成类是为了服务端类加载隔离：本类引用的 {@link FMLClientSetupEvent}、
 * {@link AccessoriesRendererRegistry}、{@link GlovesRenderer} 都只存在于客户端发行侧，
 * 服务端专用环境不应该解析它们。{@link AetherCompat} 只有在
 * {@code FMLEnvironment.dist == Dist.CLIENT} 时才会调用 {@link #register(IEventBus)}。
 * <p>
 * 天境对每个护手物品单独调用 {@code AccessoriesRendererRegistry.registerRenderer}，
 * 这里也必须逐物品注册，否则护手即使能装备也不会渲染。
 */
public final class AetherCompatClient {

    public static void register(IEventBus bus) {
        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            for (DeferredItem<Item> glove : AetherCompat.GLOVES.values()) {
                AccessoriesRendererRegistry.registerRenderer(glove.get(), GlovesRenderer::new);
            }
        }));
    }

    private AetherCompatClient() {
    }
}
