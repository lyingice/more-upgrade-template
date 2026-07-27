package net.mcreator.mut.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.mcreator.mut.affix.json.AffixJsonConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;

public class ClothConfigScreen {

    public static Screen open(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("MoreUpgradeTemplate 配置"))
                .setSavingRunnable(() -> {});

        ConfigEntryBuilder eb = builder.entryBuilder();

        // 动态遍历所有词缀构建 UI
        Map<String, ConfigCategory> catMap = new LinkedHashMap<>();
        for (AffixJsonConfig cfg : AffixJsonLoader.getAllConfigs()) {
            if (cfg.getConfigurable() == null || cfg.getConfigurable().isEmpty()) continue;
            String catKey = "affix_" + cfg.getId();
            ConfigCategory cat = builder.getOrCreateCategory(
                    Component.translatable("affix." + cfg.getId() + ".name"));
            for (AffixJsonConfig.ParamDef p : cfg.getConfigurable()) {
                cat.addEntry(eb.startDoubleField(
                        Component.translatable("config.mut.affix." + cfg.getId() + "." + p.getName()),
                        AffixConfig.getCoefficient(cfg.getId(), p.getName()))
                        .setDefaultValue(p.getDefaultValue())
                        .setMin(p.getMin()).setMax(p.getMax())
                        .build());
            }
        }

        // 等级限制
        ConfigCategory limitCat = builder.getOrCreateCategory(Component.translatable("config.mut.category.limit"));
        limitCat.addEntry(eb.startIntField(
                Component.translatable("config.mut.total_max_level"), AffixConfig.getTotalMaxLevel())
                .setDefaultValue(99).setMin(1).setMax(999).build());

        return builder.build();
    }
}