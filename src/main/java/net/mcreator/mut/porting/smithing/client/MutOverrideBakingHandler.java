package net.mcreator.mut.porting.smithing.client;

import net.mcreator.mut.MutMod;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 烘焙期把旁挂 override 注入到对应物品模型（移植自 TCR 的 OverrideBakingHandler）。
 *
 * <p>没有使用 Mixin：NeoForge 1.21.1 的 {@code ItemRenderer#getModel} 本来就会
 * 调用 {@code BakedModel#getOverrides().resolve(...)}，包装后的 overrides 足以生效。</p>
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class MutOverrideBakingHandler {

    private static final MutOverrideModelLoader LOADER = new MutOverrideModelLoader();

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);

            List<ItemOverride> overrides = LOADER.load(itemId);
            if (overrides.isEmpty()) {
                continue;
            }

            MutItemOverrides.BakedEntry[] entries = buildEntries(overrides, models);
            if (entries.length == 0) {
                continue;
            }

            ModelResourceLocation itemKey = ModelResourceLocation.inventory(itemId);
            BakedModel original = models.get(itemKey);
            if (original == null) {
                continue;
            }

            // 防止重复 reload 时二次包装。
            if (original instanceof MutOverrideBakedModel) {
                continue;
            }

            MutItemOverrides merged = new MutItemOverrides(entries, original.getOverrides());
            MutOverrideBakedModel wrapped = new MutOverrideBakedModel(original, merged);
            models.put(itemKey, wrapped);
        }
    }

    private static MutItemOverrides.BakedEntry[] buildEntries(
            List<ItemOverride> overrides,
            Map<ModelResourceLocation, BakedModel> models
    ) {
        List<MutItemOverrides.BakedEntry> entries = new ArrayList<>();

        for (ItemOverride override : overrides) {
            BakedModel target = resolveModel(override.getModel(), models);
            entries.add(new MutItemOverrides.BakedEntry(override.getPredicates().toList(), target));
        }

        return entries.toArray(new MutItemOverrides.BakedEntry[0]);
    }

    private static BakedModel resolveModel(
            ResourceLocation modelId,
            Map<ModelResourceLocation, BakedModel> models
    ) {
        return models.get(ModelResourceLocation.standalone(modelId));
    }
}
