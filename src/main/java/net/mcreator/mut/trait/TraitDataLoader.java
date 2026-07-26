package net.mcreator.mut.trait;

import com.google.gson.*;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.trait.effect.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import java.util.*;

@EventBusSubscriber(modid = MutMod.MODID)
public class TraitDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    private static final TraitDataLoader INSTANCE = new TraitDataLoader();

    private TraitDataLoader() { super(GSON, "traits"); }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager rm, ProfilerFiller profiler) {
        TraitRegistry.clear();

        for (var entry : dataMap.entrySet()) {
            JsonObject json = entry.getValue().getAsJsonObject();
            String id = json.get("id").getAsString();
            String nameKey = json.get("name_key").getAsString();

            // 解析 supported_items
            JsonArray items = json.getAsJsonArray("supported_items");
            List<String> matchItems = new ArrayList<>();
            List<TagKey<Item>> matchTags = new ArrayList<>();

            for (JsonElement e : items) {
                String s = e.getAsString();
                if (s.startsWith("#")) {
                    matchTags.add(TagKey.create(
                            net.minecraft.core.registries.Registries.ITEM,
                            ResourceLocation.parse(s.substring(1))
                    ));
                } else {
                    matchItems.add(s);
                }
            }

            // 解析 description_keys
            List<String> descKeys = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray("description_keys")) {
                descKeys.add(e.getAsString());
            }

            // 解析 effects
            List<TraitEffect> effects = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray("effects")) {
                effects.add(parseEffect(e.getAsJsonObject()));
            }

            // 注册（只注册一次）
            TraitRegistry.register(new Trait(id, nameKey, descKeys, effects, matchItems, matchTags));
        }

        MutMod.LOGGER.info("Loaded {} traits", TraitRegistry.getAll().size());
    }

    private static TraitEffect parseEffect(JsonObject obj) {
        String type = obj.get("type").getAsString();
        String trigger = obj.has("trigger") ? obj.get("trigger").getAsString() : "attack";

        return switch (type) {
            case "apply_mob_effect_to_target" -> new ApplyMobEffectToTarget(trigger,
                    obj.get("effect").getAsString(),
                    parseRange(obj.get("duration")),
                    parseRange(obj.get("amplifier")),
                    parseEntityFilter(obj));
            case "apply_mob_effect_to_self" -> new ApplyMobEffectToSelf(trigger,
                    obj.get("effect").getAsString(),
                    parseRange(obj.get("duration")),
                    parseRange(obj.get("amplifier")),
                    parseEntityFilter(obj));
            case "damage_target" -> new DamageTarget(trigger,
                    obj.get("damage_type").getAsString(),
                    parseRange(obj.get("amount")));
            case "knockback_target" -> new KnockbackTarget(trigger,
                    obj.get("strength").getAsFloat());
            case "damage_resistance" -> new DamageResistance(trigger,
                    obj.get("damage_type").getAsString(),
                    obj.get("multiplier").getAsFloat());
            case "attribute_modifier_self" -> new AttributeModifierSelf(trigger,
                    obj.get("attribute").getAsString(),
                    obj.get("operation").getAsString(),
                    obj.get("amount").getAsFloat(),
                    obj.has("duration_ticks") ? obj.get("duration_ticks").getAsInt() : 100,
                    parseEntityFilter(obj));
            case "attribute_modifier_target" -> new AttributeModifierTarget(trigger,
                    obj.get("attribute").getAsString(),
                    obj.get("operation").getAsString(),
                    obj.get("amount").getAsFloat(),
                    obj.has("duration_ticks") ? obj.get("duration_ticks").getAsInt() : 100);
            case "repair_on_block_break" -> new RepairOnBlockBreak(trigger,
                    parseBlockFilter(obj),
                    obj.get("amount").getAsInt());
            case "repair_on_hit_entity" -> new RepairOnHitEntity(trigger,
                    parseEntityFilter(obj),
                    obj.get("amount").getAsInt());
            case "repair_on_kill_entity" -> new RepairOnKillEntity(trigger,
                    parseEntityFilter(obj),
                    obj.get("amount").getAsInt());
            default -> throw new IllegalArgumentException("Unknown effect type: " + type);
        };
    }

    private static TraitEffect.ValueRange parseRange(JsonElement e) {
        if (e.isJsonPrimitive()) {
            float v = e.getAsFloat();
            return new TraitEffect.ValueRange(v, v);
        }
        JsonObject o = e.getAsJsonObject();
        return new TraitEffect.ValueRange(o.get("min").getAsFloat(), o.get("max").getAsFloat());
    }

    private static TraitEffect.EntityFilter parseEntityFilter(JsonObject obj) {
        if (!obj.has("target_entity_filter") && !obj.has("entity_filter")) return new TraitEffect.EntityFilter(null, null);
        JsonObject f = (obj.has("target_entity_filter") ? obj.getAsJsonObject("target_entity_filter")
                : obj.getAsJsonObject("entity_filter"));
        return new TraitEffect.EntityFilter(
                f.has("id") ? f.get("id").getAsString() : null,
                f.has("type") ? f.get("type").getAsString() : null);
    }

    private static TraitEffect.BlockFilter parseBlockFilter(JsonObject obj) {
        if (!obj.has("block_filter")) return new TraitEffect.BlockFilter(null, null);
        JsonObject f = obj.getAsJsonObject("block_filter");
        return new TraitEffect.BlockFilter(
                f.has("id") ? f.get("id").getAsString() : null,
                f.has("type") ? f.get("type").getAsString() : null);
    }
}