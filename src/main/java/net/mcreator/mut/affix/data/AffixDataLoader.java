package net.mcreator.mut.affix.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
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

/**
 * 词缀数据加载器 - 从 data/mut/affix/ 目录加载 JSON 配置文件
 * 使用 Minecraft 数据包重载系统，支持 /reload 命令
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class AffixDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .create();

    private static final AffixDataLoader INSTANCE = new AffixDataLoader();
    /** 上次加载的 Root 对象（用于读取 min_probability_per_level） */
    private static LevelConfig.Root lastRoot = null;

    // 缓存数据
    private static LevelConfig[] levels = new LevelConfig[0];
    private static Map<String, LevelConfig> levelMap = new HashMap<>();
    private static PityConfig pityConfig = new PityConfig();
    private static MaterialBonusConfig materialBonusConfig = new MaterialBonusConfig();
    private static ItemAffixBindingConfig itemAffixBindingConfig = new ItemAffixBindingConfig();

    // 物品→可用词缀运行时缓存
    private static ItemAffixCache itemAffixCache = new ItemAffixCache();

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

        // 1. 加载等级定义（使用 Root 解析以读取 min_probability_per_level）
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

        // 2. 加载软保底配置
        PityConfig newPity = loadSingle(dataMap, "pity_config",
                PityConfig.class, null,
                json -> GSON.fromJson(json, PityConfig.class));
        if (newPity != null) {
            pityConfig = newPity;
            MutMod.LOGGER.info("Loaded pity config: enabled={}, cap={}",
                    pityConfig.isEnabled(), pityConfig.getGlobal().getPityCap());
        }

        // 3. 加载材料加成配置
        MaterialBonusConfig newMaterial = loadSingle(dataMap, "material_bonuses",
                MaterialBonusConfig.class, null,
                json -> GSON.fromJson(json, MaterialBonusConfig.class));
        if (newMaterial != null) {
            materialBonusConfig = newMaterial;
            // 触发 MaterialBonusRegistry 缓存重建（解决 /reload 后缓存不更新问题）
            MaterialBonusRegistry.getInstance().rebuild();
            MutMod.LOGGER.info("Loaded material bonuses: {} universal, {} directed, {} tag-driven",
                    materialBonusConfig.getUniversalMaterials() != null ? materialBonusConfig.getUniversalMaterials().size() : 0,
                    materialBonusConfig.getDirectedMaterials() != null ? materialBonusConfig.getDirectedMaterials().size() : 0,
                    materialBonusConfig.getTagDrivenMaterials() != null ? materialBonusConfig.getTagDrivenMaterials().size() : 0);
        }

        // 4. 加载物品绑定
        ItemAffixBindingConfig newBinding = loadSingle(dataMap, "item_affix_bindings",
                ItemAffixBindingConfig.class, null,
                json -> GSON.fromJson(json, ItemAffixBindingConfig.class));
        if (newBinding != null) {
            itemAffixBindingConfig = newBinding;
            itemAffixCache.invalidate();
            MutMod.LOGGER.info("Loaded {} item-affix bindings",
                    itemAffixBindingConfig.getBindings() != null ? itemAffixBindingConfig.getBindings().size() : 0);
        }
    }

    // ========== 公开接口 ==========

    public static LevelConfig[] getLevels() { return levels; }

    @Nullable
    public static LevelConfig getLevel(int level) {
        return levelMap.get(String.valueOf(level));
    }

    public static int getMaxLevel() {
        return levels.length > 0 ? levels[levels.length - 1].getLevel() : 6;
    }

    /** 获取最低概率保证（从 affix_levels.json 的 min_probability_per_level） */
    public static double getMinProbability() {
        return lastRoot != null ? lastRoot.getMinProbabilityPerLevel() : 0.003;
    }

    /** 获取等级加成倍率递增步长（默认 0.3） */
    public static double getBonusMultiplierPerLevel() {
        return lastRoot != null ? lastRoot.getBonusMultiplierPerLevel() : 0.3;
    }

    public static PityConfig getPityConfig() { return pityConfig; }

    public static MaterialBonusConfig getMaterialBonusConfig() { return materialBonusConfig; }

    public static ItemAffixBindingConfig getItemAffixBindingConfig() { return itemAffixBindingConfig; }

    public static ItemAffixCache getItemAffixCache() { return itemAffixCache; }

    // ========== 辅助方法 ==========

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

    private static LevelConfig[] readLevels(JsonElement element) {
        LevelConfig[] result = GSON.fromJson(element, LevelConfig[].class);
        return result != null ? result : new LevelConfig[0];
    }
}
