package net.mcreator.mut.config;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutCrossbowLoadCountConfig {

    public static final ModConfigSpec CONFIG_SPEC;
    private static final MutCrossbowLoadCountConfig INSTANCE;

    public final ModConfigSpec.IntValue globalExtraLoadCount;
    public final ModConfigSpec.IntValue loadCountPerLevel;
    public final ModConfigSpec.ConfigValue<List<? extends String>> perCrossbowExtraLoadCounts;
    public final ModConfigSpec.IntValue multishotCount;

    private Map<String, Integer> perCrossbowCache = new HashMap<>();
    private boolean cacheValid = false;

    static {
        Pair<MutCrossbowLoadCountConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(MutCrossbowLoadCountConfig::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private MutCrossbowLoadCountConfig(ModConfigSpec.Builder builder) {
        builder.comment("弩装填数量配置",
                "最终装填数量 = Stats默认值 + 全局额外 + 指定弩额外 + (多重装填附魔等级 × 每级增量)");

        globalExtraLoadCount = builder
                .comment("全局额外装填数量，所有弩都会增加此数值",
                        "默认 0，即不额外增加")
                .defineInRange("global_extra_load_count", 0, 0, 64);

        loadCountPerLevel = builder
                .comment("多重装填附魔每级额外装填的数量",
                        "默认 1")
                .defineInRange("load_count_per_level", 1, 1, 64);

        perCrossbowExtraLoadCounts = builder
                .comment("指定弩的额外装填数量",
                        "格式: \"modid:弩id=数量\"，每行一个",
                        "此值会与全局额外装填数量加算",
                        "示例: \"mut:heavy_crossbow=2\" 表示 heavy_crossbow 在全局基础上再 +2")
                .defineListAllowEmpty("per_crossbow_extra_load_counts",
                        List::of,
                        obj -> obj instanceof String s && s.matches("^[a-z0-9_.-]+:[a-z0-9_.-]+=\\d+$")
                );

        multishotCount = builder
                .comment("多重射击附魔每次射击消耗和发射的箭矢数量",
                        "仅当弩上有多重射击附魔时生效",
                        "默认 3，与原版多重射击一致")
                .defineInRange("multishot_count", 3, 1, 64);
    }

    // ==================== 查询方法（try-catch 兜底） ====================

    public static int getGlobalExtraLoadCount() {
        try {
            return INSTANCE.globalExtraLoadCount.get();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    public static int getLoadCountPerLevel() {
        try {
            return INSTANCE.loadCountPerLevel.get();
        } catch (IllegalStateException e) {
            return 1;
        }
    }

    public static int getPerCrossbowExtraLoadCount(Item crossbow) {
        String key = crossbow.builtInRegistryHolder().key().location().toString();
        return INSTANCE.getPerCrossbowExtra(key);
    }

    public static int getTotalExtraLoadCount(Item crossbow) {
        return getGlobalExtraLoadCount() + getPerCrossbowExtraLoadCount(crossbow);
    }

    public static int getMultishotCount() {
        try {
            return INSTANCE.multishotCount.get();
        } catch (IllegalStateException e) {
            return 3;
        }
    }

    private int getPerCrossbowExtra(String registryName) {
        try {
            if (!cacheValid) {
                parsePerCrossbowEntries();
            }
        } catch (IllegalStateException e) {
            return 0;
        }
        return perCrossbowCache.getOrDefault(registryName, 0);
    }

    private void parsePerCrossbowEntries() {
        perCrossbowCache.clear();
        for (String entry : perCrossbowExtraLoadCounts.get()) {
            String[] parts = entry.split("=");
            if (parts.length == 2) {
                try {
                    int count = Integer.parseInt(parts[1]);
                    perCrossbowCache.put(parts[0], count);
                } catch (NumberFormatException ignored) {}
            }
        }
        cacheValid = true;
    }

    public static void invalidateCache() {
        INSTANCE.cacheValid = false;
    }
}