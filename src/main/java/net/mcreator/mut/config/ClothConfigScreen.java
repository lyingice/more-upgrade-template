package net.mcreator.mut.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cloth Config 配置屏幕构建器。
 * <p>
 * <b>警告：</b>此类直接 import Cloth Config 类，因此只能通过反射加载。
 * 调用方必须先检查 {@code ModList.get().isLoaded("cloth_config")}。
 */
public class ClothConfigScreen {

    // ========== 条目引用（用于保存时读取值） ==========

    // --- 印记类 ---
    private static AbstractConfigListEntry<Double> poisonMarkEntry;
    private static AbstractConfigListEntry<Double> fireMarkEntry;
    private static AbstractConfigListEntry<Double> witherMarkEntry;

    // --- 增幅类 ---
    private static AbstractConfigListEntry<Double> regenerationMarkEntry;
    private static AbstractConfigListEntry<Double> strengthBlessingEntry;
    private static AbstractConfigListEntry<Double> sharpshooterEntry;
    private static AbstractConfigListEntry<Double> piercingSpearStabEntry;
    private static AbstractConfigListEntry<Double> piercingSpearChargeEntry;
    private static AbstractConfigListEntry<Double> momentumEntry;
    private static AbstractConfigListEntry<Double> tidalSurgeWaterEntry;
    private static AbstractConfigListEntry<Double> tidalSurgeRainEntry;
    private static AbstractConfigListEntry<Double> bigStomachEntry;
    private static AbstractConfigListEntry<Double> energyConversionEntry;

    // --- 等级限制 ---
    private static AbstractConfigListEntry<Integer> totalMaxLevelEntry;

    // --- 弩装填 ---
    private static AbstractConfigListEntry<Integer> globalExtraLoadEntry;
    private static AbstractConfigListEntry<Integer> loadCountPerLevelEntry;
    private static AbstractConfigListEntry<List<String>> perCrossbowExtraEntry;
    private static AbstractConfigListEntry<Integer> multishotCountEntry;

    // ================================================

    /**
     * 构建配置屏幕。仅当 Cloth Config 已加载时调用。
     *
     * @param parent 父屏幕
     * @return 配置屏幕
     */
    public static Screen open(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("MoreUpgradeTemplate \u914D\u7F6E"))
                .setSavingRunnable(ClothConfigScreen::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        // ==================== 印记类配置 ====================
        ConfigCategory markCategory = builder.getOrCreateCategory(
                Component.translatable("config.mut.category.mark"));

        poisonMarkEntry = eb
                .startDoubleField(Component.translatable("config.mut.poison_mark"), AffixConfig.POISON_MARK_COEFFICIENT.get())
                .setDefaultValue(0.5)
                .setTooltip(Component.translatable("config.mut.poison_mark.tooltip"))
                .setMin(0.0).setMax(10.0)
                .build();
        markCategory.addEntry(poisonMarkEntry);

        fireMarkEntry = eb
                .startDoubleField(Component.translatable("config.mut.fire_mark"), AffixConfig.FIRE_MARK_COEFFICIENT.get())
                .setDefaultValue(0.5)
                .setTooltip(Component.translatable("config.mut.fire_mark.tooltip"))
                .setMin(0.0).setMax(10.0)
                .build();
        markCategory.addEntry(fireMarkEntry);

        witherMarkEntry = eb
                .startDoubleField(Component.translatable("config.mut.wither_mark"), AffixConfig.WITHER_MARK_COEFFICIENT.get())
                .setDefaultValue(0.5)
                .setTooltip(Component.translatable("config.mut.wither_mark.tooltip"))
                .setMin(0.0).setMax(10.0)
                .build();
        markCategory.addEntry(witherMarkEntry);

        // ==================== 增幅类配置 ====================
        ConfigCategory buffCategory = builder.getOrCreateCategory(
                Component.translatable("config.mut.category.buff"));

        regenerationMarkEntry = eb
                .startDoubleField(Component.translatable("config.mut.regeneration_mark"), AffixConfig.REGENERATION_MARK_COEFFICIENT.get())
                .setDefaultValue(0.5)
                .setTooltip(Component.translatable("config.mut.regeneration_mark.tooltip"))
                .setMin(0.0).setMax(10.0)
                .build();
        buffCategory.addEntry(regenerationMarkEntry);

        strengthBlessingEntry = eb
                .startDoubleField(Component.translatable("config.mut.strength_blessing"), AffixConfig.STRENGTH_BLESSING_COEFFICIENT.get())
                .setDefaultValue(0.05)
                .setTooltip(Component.translatable("config.mut.strength_blessing.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(strengthBlessingEntry);

        sharpshooterEntry = eb
                .startDoubleField(Component.translatable("config.mut.sharpshooter"), AffixConfig.SHARPSHOOTER_COEFFICIENT.get())
                .setDefaultValue(0.075)
                .setTooltip(Component.translatable("config.mut.sharpshooter.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(sharpshooterEntry);

        piercingSpearStabEntry = eb
                .startDoubleField(Component.translatable("config.mut.piercing_spear_stab"), AffixConfig.PIERCING_SPEAR_STAB_COEFFICIENT.get())
                .setDefaultValue(0.10)
                .setTooltip(Component.translatable("config.mut.piercing_spear_stab.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(piercingSpearStabEntry);

        piercingSpearChargeEntry = eb
                .startDoubleField(Component.translatable("config.mut.piercing_spear_charge"), AffixConfig.PIERCING_SPEAR_CHARGE_COEFFICIENT.get())
                .setDefaultValue(0.05)
                .setTooltip(Component.translatable("config.mut.piercing_spear_charge.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(piercingSpearChargeEntry);

        momentumEntry = eb
                .startDoubleField(Component.translatable("config.mut.momentum"), AffixConfig.MOMENTUM_COEFFICIENT.get())
                .setDefaultValue(0.125)
                .setTooltip(Component.translatable("config.mut.momentum.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(momentumEntry);

        tidalSurgeWaterEntry = eb
                .startDoubleField(Component.translatable("config.mut.tidal_surge_water"), AffixConfig.TIDAL_SURGE_WATER_COEFFICIENT.get())
                .setDefaultValue(0.10)
                .setTooltip(Component.translatable("config.mut.tidal_surge_water.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(tidalSurgeWaterEntry);

        tidalSurgeRainEntry = eb
                .startDoubleField(Component.translatable("config.mut.tidal_surge_rain"), AffixConfig.TIDAL_SURGE_RAIN_COEFFICIENT.get())
                .setDefaultValue(0.05)
                .setTooltip(Component.translatable("config.mut.tidal_surge_rain.tooltip"))
                .setMin(0.0).setMax(1.0)
                .build();
        buffCategory.addEntry(tidalSurgeRainEntry);

        bigStomachEntry = eb
                .startDoubleField(Component.translatable("config.mut.big_stomach"), AffixConfig.BIG_STOMACH_COEFFICIENT.get())
                .setDefaultValue(0.5)
                .setTooltip(Component.translatable("config.mut.big_stomach.tooltip"))
                .setMin(0.0).setMax(10.0)
                .build();
        buffCategory.addEntry(bigStomachEntry);

        energyConversionEntry = eb
                .startDoubleField(Component.translatable("config.mut.energy_conversion"), AffixConfig.ENERGY_CONVERSION_COEFFICIENT.get())
                .setDefaultValue(0.5)
                .setTooltip(Component.translatable("config.mut.energy_conversion.tooltip"))
                .setMin(0.0).setMax(10.0)
                .build();
        buffCategory.addEntry(energyConversionEntry);

        // ==================== 等级限制 ====================
        ConfigCategory limitCategory = builder.getOrCreateCategory(
                Component.translatable("config.mut.category.limit"));

        totalMaxLevelEntry = eb
                .startIntField(Component.translatable("config.mut.total_max_level"), AffixConfig.TOTAL_MAX_LEVEL.get())
                .setDefaultValue(99)
                .setTooltip(Component.translatable("config.mut.total_max_level.tooltip"))
                .setMin(1).setMax(999)
                .build();
        limitCategory.addEntry(totalMaxLevelEntry);

        // ==================== 弩装填配置 ====================
        ConfigCategory crossbowCategory = builder.getOrCreateCategory(
                Component.translatable("config.mut.category.crossbow"));

        globalExtraLoadEntry = eb
                .startIntField(Component.translatable("config.mut.global_extra_load"),
                        MutCrossbowLoadCountConfig.getGlobalExtraLoadCount())
                .setDefaultValue(0)
                .setTooltip(Component.translatable("config.mut.global_extra_load.tooltip"))
                .setMin(0).setMax(64)
                .build();
        crossbowCategory.addEntry(globalExtraLoadEntry);

        loadCountPerLevelEntry = eb
                .startIntField(Component.translatable("config.mut.load_count_per_level"),
                        MutCrossbowLoadCountConfig.getLoadCountPerLevel())
                .setDefaultValue(1)
                .setTooltip(Component.translatable("config.mut.load_count_per_level.tooltip"))
                .setMin(1).setMax(64)
                .build();
        crossbowCategory.addEntry(loadCountPerLevelEntry);

        // 读取指定弩额外装填列表
        List<String> currentExtraList = new ArrayList<>(
                (List<String>) MutCrossbowLoadCountConfig.getPerCrossbowExtraEntries()
        );

        perCrossbowExtraEntry = eb
                .startStrList(Component.translatable("config.mut.per_crossbow_extra"), currentExtraList)
                .setDefaultValue(List.of())
                .setTooltip(Component.translatable("config.mut.per_crossbow_extra.tooltip"))
                .build();
        crossbowCategory.addEntry(perCrossbowExtraEntry);

        multishotCountEntry = eb
                .startIntField(Component.translatable("config.mut.multishot_count"),
                        MutCrossbowLoadCountConfig.getMultishotCount())
                .setDefaultValue(3)
                .setTooltip(Component.translatable("config.mut.multishot_count.tooltip"))
                .setMin(1).setMax(64)
                .build();
        crossbowCategory.addEntry(multishotCountEntry);

        // 添加所有分类

        return builder.build();
    }

    // ==================== 保存逻辑 ====================

    private static void save() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        boolean changed = false;

        // ---- 写入 affix 配置 ----
        Path affixFile = configDir.resolve("mut_affix_coefficients.toml");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# MoreUpgradeTemplate - \u5370\u8BB0\u7CFB\u6570\u914D\u7F6E\n");
            sb.append("# \u6B64\u6587\u4EF6\u7531 Cloth Config GUI \u751F\u6210\uFF0C\u624B\u52A8\u4FEE\u6539\u540E\u9700\u91CD\u542F\u6E38\u620F\u751F\u6548\n\n");

            sb.append("#\u5370\u8BB0\u7C7B - \u6BCF\u7EA7\u4F24\u5BB3/\u6062\u590D\u52A0\u6210\n");
            sb.append("poison_mark_coefficient = ").append(formatDouble(poisonMarkEntry.getValue())).append("\n");
            sb.append("fire_mark_coefficient = ").append(formatDouble(fireMarkEntry.getValue())).append("\n");
            sb.append("wither_mark_coefficient = ").append(formatDouble(witherMarkEntry.getValue())).append("\n\n");

            sb.append("#\u589E\u5E45\u7C7B - \u6BCF\u7EA7\u500D\u7387/\u52A0\u6210\n");
            sb.append("regeneration_mark_coefficient = ").append(formatDouble(regenerationMarkEntry.getValue())).append("\n");
            sb.append("strength_blessing_coefficient = ").append(formatDouble(strengthBlessingEntry.getValue())).append("\n");
            sb.append("sharpshooter_coefficient = ").append(formatDouble(sharpshooterEntry.getValue())).append("\n");
            sb.append("piercing_spear_stab_coefficient = ").append(formatDouble(piercingSpearStabEntry.getValue())).append("\n");
            sb.append("piercing_spear_charge_coefficient = ").append(formatDouble(piercingSpearChargeEntry.getValue())).append("\n");
            sb.append("momentum_coefficient = ").append(formatDouble(momentumEntry.getValue())).append("\n");
            sb.append("tidal_surge_water_coefficient = ").append(formatDouble(tidalSurgeWaterEntry.getValue())).append("\n");
            sb.append("tidal_surge_rain_coefficient = ").append(formatDouble(tidalSurgeRainEntry.getValue())).append("\n");
            sb.append("big_stomach_coefficient = ").append(formatDouble(bigStomachEntry.getValue())).append("\n");
            sb.append("energy_conversion_coefficient = ").append(formatDouble(energyConversionEntry.getValue())).append("\n\n");

            sb.append("#\u7B49\u7EA7\u9650\u5236\n");
            sb.append("total_max_level = ").append(totalMaxLevelEntry.getValue()).append("\n");

            Files.writeString(affixFile, sb.toString());
            changed = true;
        } catch (IOException e) {
            net.mcreator.mut.MutMod.LOGGER.error("Failed to write affix config file", e);
        }

        // ---- 写入 crossbow 配置 ----
        Path crossbowFile = configDir.resolve("mut_crossbow_load_count.toml");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# MoreUpgradeTemplate - \u5F29\u88C5\u586B\u6570\u91CF\u914D\u7F6E\n");
            sb.append("# \u6B64\u6587\u4EF6\u7531 Cloth Config GUI \u751F\u6210\uFF0C\u624B\u52A8\u4FEE\u6539\u540E\u9700\u91CD\u542F\u6E38\u620F\u751F\u6548\n\n");

            sb.append("global_extra_load_count = ").append(globalExtraLoadEntry.getValue()).append("\n");
            sb.append("load_count_per_level = ").append(loadCountPerLevelEntry.getValue()).append("\n");

            // 字符串列表的 TOML 格式
            List<String> extraList = perCrossbowExtraEntry.getValue();
            if (extraList == null || extraList.isEmpty()) {
                sb.append("per_crossbow_extra_load_counts = []\n");
            } else {
                sb.append("per_crossbow_extra_load_counts = [");
                sb.append(extraList.stream()
                        .map(s -> "\"" + escapeTomlString(s) + "\"")
                        .collect(Collectors.joining(", ")));
                sb.append("]\n");
            }

            sb.append("multishot_count = ").append(multishotCountEntry.getValue()).append("\n");

            Files.writeString(crossbowFile, sb.toString());
            changed = true;
        } catch (IOException e) {
            net.mcreator.mut.MutMod.LOGGER.error("Failed to write crossbow config file", e);
        }

        // 失效弩配置缓存
        if (changed) {
            MutCrossbowLoadCountConfig.invalidateCache();
        }
    }

    private static String formatDouble(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((int) value);
        }
        // 保留足够精度，去掉尾部冗余0
        String s = String.format("%.10f", value).replaceAll("0+$", "");
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static String escapeTomlString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
