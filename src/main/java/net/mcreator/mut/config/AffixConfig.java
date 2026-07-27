package net.mcreator.mut.config;

import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.ArrayList;
import java.util.List;

public class AffixConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> AFFIX_COEFFICIENT_OVERRIDES;
    public static final ModConfigSpec.IntValue TOTAL_MAX_LEVEL;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("词缀系数覆盖");
        AFFIX_COEFFICIENT_OVERRIDES = BUILDER
                .comment("格式: affix_id:param_name=value",
                         "例: fire_mark:per_level=0.5, momentum:per_level=0.125")
                .defineList("affix_coefficient_overrides", ArrayList::new,
                        e -> e instanceof String s && s.matches("^[a-z_]+:[a-z_]+=[0-9.]+$"));
        BUILDER.pop();

        BUILDER.push("等级限制");
        TOTAL_MAX_LEVEL = BUILDER.comment("全身装备总等级上限")
                .defineInRange("total_max_level", 99, 1, 999);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static double getCoefficient(String affixId, String paramName) {
        for (String override : AFFIX_COEFFICIENT_OVERRIDES.get()) {
            if (override.startsWith(affixId + ":" + paramName + "=")) {
                try { return Double.parseDouble(override.split("=")[1]); }
                catch (NumberFormatException e) { }
            }
        }
        return AffixJsonLoader.getDefault(affixId, paramName);
    }

    @Deprecated
    public static double getCoefficient(String affixId) { return getCoefficient(affixId, "per_level"); }
    public static int getTotalMaxLevel() { return TOTAL_MAX_LEVEL.get(); }
}