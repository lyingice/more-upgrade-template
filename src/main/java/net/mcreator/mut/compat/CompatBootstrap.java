package net.mcreator.mut.compat;

import net.mcreator.mut.MutMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/**
 * 联动入口 —— 这个类会被无条件加载，所以里面不能出现农夫乐事 / 天境的任何类型。
 * <p>
 * 真正的联动代码放在 compat.fd / compat.aether 两个包里，只有对应模组存在时才反射加载；
 * 模组缺席时那些类根本不会被 JVM 解析，不会出现 NoClassDefFoundError。
 */
public final class CompatBootstrap {

    private static final String FD_CLASS = "net.mcreator.mut.compat.fd.FdCompat";
    private static final String AETHER_CLASS = "net.mcreator.mut.compat.aether.AetherCompat";

    public static void init(IEventBus modEventBus) {
        if (ModList.get().isLoaded("farmersdelight")) {
            load(FD_CLASS, modEventBus, "Farmer's Delight");
        }
        if (ModList.get().isLoaded("aether")) {
            if (ModList.get().isLoaded("accessories")) {
                load(AETHER_CLASS, modEventBus, "Aether");
            } else {
                MutMod.LOGGER.warn("[compat] aether present but accessories missing, skip gloves");
            }
        }
    }

    private static void load(String className, IEventBus bus, String label) {
        try {
            Class.forName(className).getMethod("register", IEventBus.class).invoke(null, bus);
        } catch (Throwable t) {
            MutMod.LOGGER.error("[compat] {} integration failed to load, skipped", label, t);
        }
    }

    private CompatBootstrap() {
    }
}
