package net.mcreator.mut.config;

import net.mcreator.mut.MutMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * 客户端配置 GUI 注册入口。
 * <p>
 * 仅在 {@code cloth_config} 模组已加载时，向模组列表注册"配置"按钮。
 * 借助 Gradle 的 {@code implementation fileTree(dir: 'libs', ...)}，
 * Cloth Config 的类在编译期和运行期均可用，因此 {@link ClothConfigScreen}
 * 可以直接被 JVM 加载而不会报 {@code NoClassDefFoundError}。
 * <p>
 * 本类本身的 import 中不含任何 Cloth Config 类，因此即使 Cloth Config
 * 未加载，本类也可以安全被加载（仅跳过注册逻辑）。
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class ClothConfigSetup {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        if (!ModList.get().isLoaded("cloth_config")) {
            MutMod.LOGGER.info("Cloth Config not detected, skipping config GUI registration");
            return;
        }

        MutMod.LOGGER.info("Cloth Config detected, registering config GUI");

        var modContainer = ModList.get().getModContainerById(MutMod.MODID);
        modContainer.ifPresent(container ->
                container.registerExtensionPoint(
                        IConfigScreenFactory.class,
                        (IConfigScreenFactory) (modContainer1, parent) ->
                                ClothConfigScreen.open(parent)
                )
        );
    }
}
