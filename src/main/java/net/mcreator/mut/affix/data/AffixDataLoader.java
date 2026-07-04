package net.mcreator.mut.affix.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.mcreator.mut.MutMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@EventBusSubscriber(modid = MutMod.MODID)
public class AffixDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final AffixDataLoader INSTANCE = new AffixDataLoader();
    private static LevelConfig.Root lastRoot = null;

    private static LevelConfig[] levels = new LevelConfig[0];
    private static Map<String, LevelConfig> levelMap = new HashMap<>();
    private static PityConfig pityConfig = new PityConfig();
    private static MaterialBonusConfig materialBonusConfig = new MaterialBonusConfig();
    private static ItemAffixBindingConfig itemAffixBindingConfig = new ItemAffixBindingConfig();

    private static ItemAffixCache itemAffixCache = new ItemAffixCache();

    private static boolean newPoolSystem = false;
    private static double totalPoolScaleFactor = 0;
    private static double totalPoolBase = 0;
    private static int defaultMaxLevelCap = 5;

    private AffixDataLoader() {
        super(GSON, "affix");
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap,
                         ResourceManager resourceManager,
                         ProfilerFiller profiler) {
        MutMod.LOGGER.info("Loading affix data from data/mut/affix/...");

        lastRoot = loadSingle(dataMap, "affix_levels",
                LevelConfig.Root.class, null,
                json -> GSON.fromJson(json, LevelConfig.Root.class));
        LevelConfig[] newLevels = lastRoot != null ? lastRoot.getLevels() : null;
        if (newLevels != null) {
            levels = newLevels;
            levelMap = new HashMap<>();
            for (LevelConfig lc : levels) {
                levelMap.put(String.valueOf(lc.getLevel()), lc);
            }
            MutMod.LOGGER.info("Loaded {} affix levels", levels.length);
        }

        if (lastRoot != null && lastRoot.isNewPoolSystem()) {
            newPoolSystem = true;
            totalPoolScaleFactor = lastRoot.getTotalPoolScaleFactor();
            totalPoolBase = lastRoot.getTotalPoolBase();
            defaultMaxLevelCap = lastRoot.getDefaultMaxLevelCap();
            MutMod.LOGGER.info("New pool system: scaleFactor={}, basePool={}, defaultMaxCap={}",
                    totalPoolScaleFactor, totalPoolBase, defaultMaxLevelCap);
        } else {
            newPoolSystem = false;
            totalPoolScaleFactor = 0;
            totalPoolBase = 0;
            defaultMaxLevelCap = 5;
        }

        PityConfig newPity = loadSingle(dataMap, "pity_config",
                PityConfig.class, null,
                json -> GSON.fromJson(json, PityConfig.class));
        if (newPity != null) {
            pityConfig = newPity;
        }

        MaterialBonusConfig newMaterial = loadSingle(dataMap, "material_bonuses",
                MaterialBonusConfig.class, null,
                json -> GSON.fromJson(json, MaterialBonusConfig.class));
        if (newMaterial != null) {
            materialBonusConfig = newMaterial;
            MaterialBonusRegistry.getInstance().rebuild();
            MutMod.LOGGER.info("Loaded material bonuses: {} universal, {} directed, {} tag-driven",
                    materialBonusConfig.getUniversalMaterials() != null ? materialBonusConfig.getUniversalMaterials().size() : 0,
                    materialBonusConfig.getDirectedMaterials() != null ? materialBonusConfig.getDirectedMaterials().size() : 0,
                    materialBonusConfig.getTagDrivenMaterials() != null ? materialBonusConfig.getTagDrivenMaterials().size() : 0);
        }

        ItemAffixBindingConfig newBinding = loadSingle(dataMap, "item_affix_bindings",
                ItemAffixBindingConfig.class, null,
                json -> GSON.fromJson(json, ItemAffixBindingConfig.class));
        if (newBinding != null) {
            itemAffixBindingConfig = newBinding;
            itemAffixCache.invalidate();
        }
    }

    public static LevelConfig[] getLevels() { return levels; }

    @Nullable
    public static LevelConfig getLevel(int level) {
        return levelMap.get(String.valueOf(level));
    }

    public static int getMaxLevel() {
        return levels.length > 0 ? levels[levels.length - 1].getLevel() : 6;
    }

    public static double getMinProbability() {
        return lastRoot != null ? lastRoot.getMinProbabilityPerLevel() : 0.003;
    }

    public static double getBonusMultiplierPerLevel() {
        return lastRoot != null ? lastRoot.getBonusMultiplierPerLevel() : 0.3;
    }

    public static boolean isNewPoolSystem() { return newPoolSystem; }
    public static double getTotalPoolScaleFactor() { return totalPoolScaleFactor; }
    public static double getTotalPoolBase() { return totalPoolBase; }
    public static int getDefaultMaxLevelCap() { return defaultMaxLevelCap; }

    public static PityConfig getPityConfig() { return pityConfig; }
    public static MaterialBonusConfig getMaterialBonusConfig() { return materialBonusConfig; }
    public static ItemAffixBindingConfig getItemAffixBindingConfig() { return itemAffixBindingConfig; }
    public static ItemAffixCache getItemAffixCache() { return itemAffixCache; }

    @Nullable
    private static <T> T loadSingle(Map<ResourceLocation, JsonElement> dataMap,
                                    String fileName, Class<?> configClass,
                                    @Nullable String subField,
                                    Function<JsonElement, T> parser) {
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath("mut", fileName);
        JsonElement element = dataMap.get(key);
        if (element == null) {
            MutMod.LOGGER.warn("No {} configuration found! Using defaults.", fileName);
            return null;
        }
        try {
            JsonElement target = element;
            if (subField != null && element.isJsonObject()) {
                var obj = element.getAsJsonObject();
                if (obj.has(subField)) {
                    target = obj.get(subField);
                } else {
                    MutMod.LOGGER.warn("{} missing '{}' field", fileName, subField);
                    return null;
                }
            }
            return parser.apply(target);
        } catch (Exception e) {
            MutMod.LOGGER.error("Failed to parse {}: {}", fileName, e.getMessage());
            return null;
        }
    }
}
