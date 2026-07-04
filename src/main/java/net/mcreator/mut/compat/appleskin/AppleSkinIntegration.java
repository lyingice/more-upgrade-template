package net.mcreator.mut.compat.appleskin;

import net.mcreator.mut.MutMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * AppleSkin 可选集成注册入口。
 * <p>
 * 本类不含任何 AppleSkin 类的直接 import，因此即使 AppleSkin 未安装也可安全加载。
 * 在客户端启动时检查 {@code appleskin} 模组是否已加载，再注册实际的事件处理器。
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class AppleSkinIntegration {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        if (!ModList.get().isLoaded("appleskin")) {
            MutMod.LOGGER.info("AppleSkin not detected, skipping food values integration");
            return;
        }

        MutMod.LOGGER.info("AppleSkin detected, registering food values handler");

        // AppleSkinFoodValuesHandler 的 import 类（squeek.appleskin.*）在 libs/ 目录的
        // appleskin JAR 中，已通过 implementation fileTree 添加至运行时 classpath，
        // 因此即使 AppleSkin 模组未安装，该类也可正常加载。本处仅通过 isLoaded 检查
        // 来避免无意义的注册。
        NeoForge.EVENT_BUS.register(new AppleSkinFoodValuesHandler());
    }
}
