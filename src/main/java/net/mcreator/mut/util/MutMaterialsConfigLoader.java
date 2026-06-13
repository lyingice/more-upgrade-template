package net.mcreator.mut.util;

import com.google.gson.*;
import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态覆盖 MutMaterials 配置的加载器
 * 数据包中的 JSON 会覆盖硬编码的材质配置
 */
public class MutMaterialsConfigLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, JsonObject> PENDING_CONFIGS = new HashMap<>();

    public MutMaterialsConfigLoader() {
        super(GSON, "mut_materials");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager manager, ProfilerFiller profiler) {
        PENDING_CONFIGS.clear();
        for (var entry : jsonMap.entrySet()) {
            String name = entry.getKey().getPath();
            JsonObject obj = entry.getValue().getAsJsonObject();
            PENDING_CONFIGS.put(name, obj);
            System.out.println("[MutMaterials] Loaded datapack config for: " + name);
        }

        // 应用配置到 MutMaterials（覆盖现有材质）
        applyConfigsToMutMaterials();
    }

    private void applyConfigsToMutMaterials() {
        // 通过反射或直接调用 MutMaterials 的更新方法
        // 由于 MutMaterials 的材质是 final 的，我们需要在 MutMaterials 中添加一个更新方法
        MutMaterials.applyDataPackConfigs(PENDING_CONFIGS);
    }

    public static JsonObject getPendingConfig(String name) {
        return PENDING_CONFIGS.get(name);
    }

    @EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
    public static class ReloadListener {
        @SubscribeEvent
        public static void onAddReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new MutMaterialsConfigLoader());
        }
    }
}