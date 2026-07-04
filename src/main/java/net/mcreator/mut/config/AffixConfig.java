package net.mcreator.mut.config;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.mcreator.mut.MutMod;

import java.nio.file.Path;

public class AffixConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ========== 印记类：每级伤害/恢复加成 ==========
    public static final ModConfigSpec.DoubleValue POISON_MARK_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue FIRE_MARK_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue WITHER_MARK_COEFFICIENT;

    // ========== 增幅类：每级倍率加成 ==========
    public static final ModConfigSpec.DoubleValue REGENERATION_MARK_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue STRENGTH_BLESSING_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue SHARPSHOOTER_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue PIERCING_SPEAR_STAB_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue PIERCING_SPEAR_CHARGE_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue MOMENTUM_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue TIDAL_SURGE_WATER_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue TIDAL_SURGE_RAIN_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue BIG_STOMACH_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue ENERGY_CONVERSION_COEFFICIENT;

    // ========== 等级限制 ==========
    public static final ModConfigSpec.IntValue TOTAL_MAX_LEVEL;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("印记类 - 每级伤害/恢复加成");
        POISON_MARK_COEFFICIENT = BUILDER
                .comment("剧毒印记：每级中毒伤害加成")
                .defineInRange("poison_mark_coefficient", 0.5, 0.0, 10.0);
        FIRE_MARK_COEFFICIENT = BUILDER
                .comment("灼烧印记：每级火焰伤害加成")
                .defineInRange("fire_mark_coefficient", 0.5, 0.0, 10.0);
        WITHER_MARK_COEFFICIENT = BUILDER
                .comment("凋零印记：每级凋零伤害加成")
                .defineInRange("wither_mark_coefficient", 0.5, 0.0, 10.0);
        BUILDER.pop();

        BUILDER.push("增幅类 - 每级倍率/加成");
        REGENERATION_MARK_COEFFICIENT = BUILDER
                .comment("再生印记：每级生命恢复加成")
                .defineInRange("regeneration_mark_coefficient", 0.5, 0.0, 10.0);
        STRENGTH_BLESSING_COEFFICIENT = BUILDER
                .comment("力量祝福：每级近战伤害倍率")
                .defineInRange("strength_blessing_coefficient", 0.05, 0.0, 1.0);
        SHARPSHOOTER_COEFFICIENT = BUILDER
                .comment("神射手：每级弹射物伤害倍率")
                .defineInRange("sharpshooter_coefficient", 0.075, 0.0, 1.0);
        PIERCING_SPEAR_STAB_COEFFICIENT = BUILDER
                .comment("贯穿之矛：每级戳击伤害倍率")
                .defineInRange("piercing_spear_stab_coefficient", 0.10, 0.0, 1.0);
        PIERCING_SPEAR_CHARGE_COEFFICIENT = BUILDER
                .comment("贯穿之矛：每级冲刺伤害倍率")
                .defineInRange("piercing_spear_charge_coefficient", 0.05, 0.0, 1.0);
        MOMENTUM_COEFFICIENT = BUILDER
                .comment("势能印记：每级下落攻击伤害倍率")
                .defineInRange("momentum_coefficient", 0.125, 0.0, 1.0);
        TIDAL_SURGE_WATER_COEFFICIENT = BUILDER
                .comment("潮涌之力：每级水中攻击倍率")
                .defineInRange("tidal_surge_water_coefficient", 0.10, 0.0, 1.0);
        TIDAL_SURGE_RAIN_COEFFICIENT = BUILDER
                .comment("潮涌之力：每级雨天攻击倍率")
                .defineInRange("tidal_surge_rain_coefficient", 0.05, 0.0, 1.0);
        BIG_STOMACH_COEFFICIENT = BUILDER
                .comment("大胃袋：每级自然恢复加成")
                .defineInRange("big_stomach_coefficient", 0.5, 0.0, 10.0);
        ENERGY_CONVERSION_COEFFICIENT = BUILDER
                .comment("能量转化：每级耐久恢复加成")
                .defineInRange("energy_conversion_coefficient", 0.5, 0.0, 10.0);
        BUILDER.pop();

        BUILDER.push("等级限制");
        TOTAL_MAX_LEVEL = BUILDER
                .comment("全身装备总等级上限")
                .defineInRange("total_max_level", 99, 1, 999);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // ========== 便捷获取 ==========
    public static double getCoefficient(String affixId) {
        return switch (affixId) {
            case "poison_mark" -> POISON_MARK_COEFFICIENT.get();
            case "fire_mark" -> FIRE_MARK_COEFFICIENT.get();
            case "wither_mark" -> WITHER_MARK_COEFFICIENT.get();
            case "regeneration_mark" -> REGENERATION_MARK_COEFFICIENT.get();
            case "strength_blessing" -> STRENGTH_BLESSING_COEFFICIENT.get();
            case "sharpshooter" -> SHARPSHOOTER_COEFFICIENT.get();
            case "piercing_spear_stab" -> PIERCING_SPEAR_STAB_COEFFICIENT.get();
            case "piercing_spear_charge" -> PIERCING_SPEAR_CHARGE_COEFFICIENT.get();
            case "momentum" -> MOMENTUM_COEFFICIENT.get();
            case "tidal_surge_water" -> TIDAL_SURGE_WATER_COEFFICIENT.get();
            case "tidal_surge_rain" -> TIDAL_SURGE_RAIN_COEFFICIENT.get();
            case "big_stomach" -> BIG_STOMACH_COEFFICIENT.get();
            case "energy_conversion" -> ENERGY_CONVERSION_COEFFICIENT.get();
            default -> 0.5;
        };
    }
    public static int getTotalMaxLevel() { return TOTAL_MAX_LEVEL.get(); }
}