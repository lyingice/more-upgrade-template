package net.mcreator.mut.armorset;

import com.google.gson.*;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.armorset.effect.RepairArmorEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * data/mut/armor_sets/*.json 的加载器。支持 /reload 热重载。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class ArmorSetDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    private static final ArmorSetDataLoader INSTANCE = new ArmorSetDataLoader();

    private ArmorSetDataLoader() {
        super(GSON, "armor_sets");
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager rm, ProfilerFiller profiler) {
        ArmorSetRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : dataMap.entrySet()) {
            JsonObject json = entry.getValue().getAsJsonObject();
            String id = json.get("id").getAsString();
            String nameKey = json.get("name_key").getAsString();

            List<String> pieceItems = new ArrayList<>();
            List<TagKey<Item>> pieceTags = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray("pieces")) {
                String s = e.getAsString();
                if (s.startsWith("#")) {
                    pieceTags.add(TagKey.create(Registries.ITEM, ResourceLocation.parse(s.substring(1))));
                } else {
                    pieceItems.add(s);
                }
            }

            List<ArmorSet.Bonus> bonuses = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray("bonuses")) {
                JsonObject b = e.getAsJsonObject();
                int pieces = b.get("pieces").getAsInt();
                String descKey = b.has("desc_key") ? b.get("desc_key").getAsString() : "";
                List<ArmorSetEffect> effects = new ArrayList<>();
                if (b.has("effects")) {
                    for (JsonElement ef : b.getAsJsonArray("effects")) {
                        effects.add(parseEffect(ef.getAsJsonObject()));
                    }
                }
                bonuses.add(new ArmorSet.Bonus(pieces, descKey, effects));
            }

            ArmorSetRegistry.register(new ArmorSet(id, nameKey, pieceItems, pieceTags, bonuses));
        }

        MutMod.LOGGER.info("Loaded {} armor sets", ArmorSetRegistry.getAll().size());
    }

    private static ArmorSetEffect parseEffect(JsonObject obj) {
        String type = obj.get("type").getAsString();
        String trigger = obj.has("trigger") ? obj.get("trigger").getAsString() : "hurt";

        if ("repair_armor".equals(type)) {
            int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
            int interval = obj.has("interval_seconds") ? obj.get("interval_seconds").getAsInt() : 0;
            float chance = obj.has("chance") ? obj.get("chance").getAsFloat() : 1.0F;
            return new RepairArmorEffect(trigger, amount, interval, chance);
        }
        throw new IllegalArgumentException("Unknown armor set effect type: " + type);
    }
}
