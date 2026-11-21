package net.mcreator.mut.porting.smithing.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 解析旁挂的 {@code overrides/item/<item_id>.json}（移植自 TCR 的 OverrideModelLoader）。
 *
 * <p>这些 JSON 不在 {@code models} 路径下，原版 ModelBakery 不会读取它们，
 * 因此不会覆盖原版物品模型，只影响本模组注入的 ItemOverrides。</p>
 */
public class MutOverrideModelLoader {

    public List<ItemOverride> load(ResourceLocation itemId) {
        List<ItemOverride> result = new ArrayList<>();

        ResourceLocation overrideLocation = getOverrideLocation(itemId);

        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = manager.getResource(overrideLocation);

        if (resource.isEmpty()) {
            return result;
        }

        try (var reader = new InputStreamReader(resource.get().open())) {
            JsonElement element = JsonParser.parseReader(reader);

            if (!(element instanceof JsonObject root)) {
                return result;
            }
            if (!root.has("overrides")) {
                return result;
            }

            JsonArray overrides = root.getAsJsonArray("overrides");

            for (JsonElement overrideElement : overrides) {
                if (!(overrideElement instanceof JsonObject overrideJson)) {
                    continue;
                }
                if (!overrideJson.has("predicate") || !overrideJson.has("model")) {
                    continue;
                }

                JsonObject predicateJson = overrideJson.getAsJsonObject("predicate");
                List<ItemOverride.Predicate> predicates = new ArrayList<>();
                boolean valid = true;

                for (Map.Entry<String, JsonElement> entry : predicateJson.entrySet()) {
                    JsonElement value = entry.getValue();
                    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
                        valid = false;
                        break;
                    }
                    predicates.add(new ItemOverride.Predicate(
                            ResourceLocation.parse(entry.getKey()),
                            value.getAsFloat()
                    ));
                }

                if (!valid || predicates.isEmpty()) {
                    continue;
                }

                ResourceLocation model = ResourceLocation.parse(overrideJson.get("model").getAsString());

                result.add(new ItemOverride(model, predicates));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ResourceLocation getOverrideLocation(ResourceLocation itemId) {
        return ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                "overrides/item/" + itemId.getPath() + ".json"
        );
    }
}
