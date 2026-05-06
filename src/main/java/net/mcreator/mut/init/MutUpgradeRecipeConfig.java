package net.mcreator.mut.init;

import java.util.*;

/**
 * 锻造台升级配方配置
 * <p>
 * 定义每种目标材质可以从哪些旧材质升级而来，
 * 以及所需的锻造模板和附加材料。
 * <p>
 * 由 {@code MutRecipeGenerator} 读取并生成对应的
 * {@code smithing_transform} JSON 配方文件。
 */
public class MutUpgradeRecipeConfig {

    /**
     * 一个升级配方组的配置
     *
     * @param targetMaterial 目标材质名称（对应 {@link MutMaterials.MutMaterial#name()}）
     * @param fromMaterials  可以从哪些旧材质升级（对应旧材质的 name）
     * @param template       锻造模板物品 ID（如 "mut:steel_upgrade_template"）
     * @param addition       附加材料物品 ID（如 "mut:steel_ingot"）
     */
    public record UpgradeGroup(
            String targetMaterial,
            List<String> fromMaterials,
            String template,
            String addition
    ) {}

    // =====================================================================
    // 注册表
    // =====================================================================

    private static final List<UpgradeGroup> GROUPS = new ArrayList<>();

    // =====================================================================
    // 注册方法
    // =====================================================================

    /**
     * 注册一个升级配方组。
     *
     * @param targetMaterial 目标材质名
     * @param fromMaterials  来源材质名列表
     * @param template       模板物品 ID
     * @param addition       附加材料物品 ID
     */
    public static void register(
            String targetMaterial,
            List<String> fromMaterials,
            String template,
            String addition
    ) {
        GROUPS.add(new UpgradeGroup(targetMaterial, List.copyOf(fromMaterials), template, addition));
    }

    /**
     * 获取所有升级配方组。
     */
    public static List<UpgradeGroup> getAll() {
        return Collections.unmodifiableList(GROUPS);
    }

    // =====================================================================
    // 预设配置（已迁移至 MCR，保留供后续版本迁移使用）
    // =====================================================================
    /*
    static {
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
                List.of("golden", "golden_chain"),
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
                "mut:nether_star"
        );

        // ────────── 下界之星：从下界合金升级 ──────────
        register("nether_star",
                List.of("netherite"),
                "mut:nether_star_upgrade_template",
                "mut:nether_star"
        );

        // ────────── 下界合金红石：从下界合金和下界合金链升级 ──────────
        register("netherite_redstone",
                List.of("netherite", "netherite_chain"),
                "mut:netherite_redstone_upgrade_template",
                "mut:netherite_redstone_ingot"
        );

        // ────────── 下界合金绿宝石：从绿宝石升级 ──────────
        register("netherite_emerald",
                List.of("emerald_armor"),
                "mut:netherite_emerald_upgrade_smithing_template",
                "mut:netherite_emerald_ingot"
        );

        // ────────── 下界合金铜：从铜升级 ──────────
        register("netherite_copper",
                List.of("copper"),
                "mut:netherite_copper_upgrade_smithing_template",
                "minecraft:copper_block"
        );

        // ==================== 模板 ====================
        //
        // register("目标材质",
        //         List.of("来源材质1", "来源材质2"),
        //         "模组:升级模板",
        //         "模组:附加材料"
        // );
        //
    }
    */
    static {
        // register("新材质",
        //         List.of("来源材质"),
        //         "mut:xxx_upgrade_template",
        //         "mut:xxx_ingot"
        // );
    }
}