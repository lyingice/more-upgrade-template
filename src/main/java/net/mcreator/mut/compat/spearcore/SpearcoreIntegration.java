package net.mcreator.mut.compat.spearcore;

import net.mcreator.mut.MutMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/**
 * spearcore 可选集成统一入口。
 * <p>
 * 本类不含 spearcore 类的直接 import，因此未安装 spearcore 时也可安全加载。
 * 仅在检测到 spearcore 后，才触发长矛物品注册等依赖 spearcore 的路径。
 */
public final class SpearcoreIntegration {

    public static final String MODID = "spearcore";

    private SpearcoreIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    /**
     * 模组初始化阶段调用：启用前置时注册 mut 长矛物品。
     */
    public static void bootstrap(IEventBus modEventBus) {
        if (!isLoaded()) {
            MutMod.LOGGER.info("spearcore not detected, skipping mut spear registration");
            return;
        }

        MutMod.LOGGER.info("spearcore detected, registering mut spears");
        // 延迟到独立类，避免未安装 spearcore 时加载 BaseSpearItem / MutSpearStats
        MutSpearItems.register();
    }
}
