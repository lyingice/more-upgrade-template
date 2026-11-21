package net.mcreator.mut.entity.spawn;

import com.google.gson.*;
import net.mcreator.mut.MutMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * data/mut/spawn_combos/*.json 的加载器，支持 /reload。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class SpawnComboLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    private static final SpawnComboLoader INSTANCE = new SpawnComboLoader();

    private SpawnComboLoader() {
        super(GSON, "spawn_combos");
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager rm, ProfilerFiller profiler) {
        SpawnComboRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : dataMap.entrySet()) {
            JsonObject json = entry.getValue().getAsJsonObject();
            String id = json.get("id").getAsString();
            ResourceLocation host = ResourceLocation.parse(json.get("host").getAsString());
            ResourceLocation rider = ResourceLocation.parse(json.get("rider").getAsString());
            float chance = json.has("chance") ? json.get("chance").getAsFloat() : 1.0F;
            int minCount = 1;
            int maxCount = 1;
            if (json.has("count")) {
                JsonObject count = json.getAsJsonObject("count");
                minCount = count.has("min") ? count.get("min").getAsInt() : 1;
                maxCount = count.has("max") ? count.get("max").getAsInt() : minCount;
            }
            Set<MobSpawnType> types = new HashSet<>();
            if (json.has("spawn_types")) {
                for (JsonElement e : json.getAsJsonArray("spawn_types")) {
                    types.add(MobSpawnType.valueOf(e.getAsString()));
                }
            }
            SpawnComboRegistry.register(new SpawnCombo(id, host, rider, chance, minCount, maxCount, types));
        }

        MutMod.LOGGER.info("Loaded {} spawn combos", SpawnComboRegistry.getAll().size());
    }
}
