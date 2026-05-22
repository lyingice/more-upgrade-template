package net.mcreator.mut.init;

import java.util.*;

/**
 * 锻造台升级配方配置
 */
public class MutUpgradeRecipeConfig {

    public record UpgradeGroup(
            String targetMaterial,
            List<String> fromMaterials,
            String template,
            String addition
    ) {}

    private static final List<UpgradeGroup> GROUPS = new ArrayList<>();

    public static void register(
            String targetMaterial,
            List<String> fromMaterials,
            String template,
            String addition
    ) {
        GROUPS.add(new UpgradeGroup(targetMaterial, List.copyOf(fromMaterials), template, addition));
    }

    public static List<UpgradeGroup> getAll() {
        return Collections.unmodifiableList(GROUPS);
    }

    static {
        // ────────── 钢：从铁升级 ──────────
        register("netherite",
                List.of("diamond"),
                "minecraft:netherite_upgrade_smithing_template",
                "minecraft:netherite_ingot"
        );
        // ────────── 钢：从铁升级 ──────────
        register("steel",
                List.of("iron"),
                "mut:steel_upgrade_template",
                "mut:steel_ingot"
        );

        // ────────── 精钢：从钢升级 ──────────
        register("advanced_steel",
                List.of("steel"),
                "minecraft:netherite_upgrade_smithing_template",
                "mut:advanced_steel_ingot"
        );

        // ────────── 鎏金：从金和金链升级 ──────────
        register("gilding",
                List.of("golden" /*,"golden_chain"*/),
                "mut:gilding_upgrade_template",
                "mut:gilding_ingot"
        );

        // ────────── 蓝钻合金：从钻石和钢升级 ──────────
        register("blue_diamond",
                List.of("diamond", "steel"),
                "mut:blue_diamond_upgrade_template",
                "mut:blue_diamond_ingot"
        );

        // ────────── 黑曜石：从钻石升级 ──────────
        register("obsidian",
                List.of("diamond"),
                "mut:obsidian_upgrade_template",
                "mut:obsidian_ingot"
        );

        // ────────── 下界合金黑曜石：从黑曜石升级 ──────────
        register("netherite_obsidian",
                List.of("obsidian"),
                "mut:netherite_obsidian_upgrade_template",
                "minecraft:netherite_ingot"
        );

        // ────────── 悲悯黑曜石：从下界合金黑曜石升级 ──────────
        register("crying_obsidian",
                List.of("netherite_obsidian"),
                "mut:crying_obsidian_upgrade_template",
                "mut:crying_obsidian_ingot"
        );

        // ────────── 龙：从蓝钻合金和精钢升级 ──────────
        register("dragon",
                List.of("blue_diamond", "advanced_steel"),
                "mut:dragon_upgrade_template",
                "minecraft:nether_star"
        );

        // ────────── 下界之星：从下界合金升级 ──────────
        register("nether_star",
                List.of("netherite"),
                "mut:nether_star_upgrade_template",
                "minecraft:nether_star"
        );
        //凋零
        register("wither",
                List.of("nether_star"),
                "mut:nether_star_upgrade_template",
                "minecraft:wither_skeleton_skull"
        );

        // ────────── 下界合金红石：从下界合金和下界合金链升级 ──────────
        register("netherite_redstone",
                List.of("netherite", "netherite_chain"),
                "mut:netherite_redstone_upgrade_template",
                "mut:netherite_redstone_ingot"
        );

        // ────────── 下界合金绿宝石：从绿宝石升级 ──────────
        register("netherite_emerald",
                List.of("emerald"),
                "mut:netherite_emerald_upgrade_smithing_template",
                "mut:netherite_emerald_ingot"
        );

        // ────────── 下界合金铜：从铜升级 ──────────
        register("netherite_copper",
                List.of("copper"),
                "mut:netherite_copper_upgrade_smithing_template",
                "minecraft:copper_block"
        );
        // ────────── 下界合金紫水晶：从紫水晶升级 ──────────
        register("netherite_amethyst",
                List.of("amethyst"),
                "mut:netherite_amethyst_upgrade_smithing_template",
                "mut:netherite_amethyst_ingot"
        );
    }
}