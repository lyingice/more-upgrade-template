package net.mcreator.mut.init;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 配方自动生成器 + 创造标签页代码生成器（纯字符串模式）
 */
public class MutRecipeGenerator {

    private static final String RECIPE_DIR = "src/main/resources/data/mut/recipe";
    private static final String STICK = "minecraft:stick";

    private static final String[] MATERIALS = {
            //大写
    };

    private record ItemType(String idSuffix, String[] pattern, String category, boolean isTwoSlot) {}

    private static final ItemType[] ITEM_TYPES = {
            new ItemType("sword",       new String[]{"a","a","b"},           "misc",   false),
            new ItemType("shovel",      new String[]{"a","b","b"},           "misc",   false),
            new ItemType("pickaxe",     new String[]{"aaa"," b "," b "},     "misc",   false),
            new ItemType("axe",         new String[]{"aa","ab"," b"},        "misc",   false),
            new ItemType("hoe",         new String[]{"aa"," b"," b"},        "misc",   false),
            new ItemType("helmet",      new String[]{"aaa","a a"},           "combat", true),
            new ItemType("chestplate",  new String[]{"a a","aaa","aaa"},     "combat", true),
            new ItemType("leggings",    new String[]{"aaa","a a","a a"},     "combat", true),
            new ItemType("boots",       new String[]{"a a","a a"},           "combat", true),
    };

    public static void main(String[] args) {
        System.out.println("=== MutRecipeGenerator 开始生成 ===\n");

        try { Files.createDirectories(Paths.get(RECIPE_DIR)); } catch (IOException ignored) {}

        int recipeCount = 0;

        for (String constName : MATERIALS) {
            MatMeta m = loadMaterial(constName);
            if (m == null) {
                System.err.println("  错误：未找到材质数据 " + constName);
                continue;
            }
            switch (m.craftingType()) {
                case "NORMAL"  -> recipeCount += generateNormalRecipes(m);
                case "SMITHING" -> recipeCount += generateSmithingRecipes(m);
            }
        }

        System.out.println("=== 配方生成完成！共 " + recipeCount + " 个文件 ===\n");

        // ═══════════════════════════════════════════
        // 控制台输出创造标签页代码
        // ═══════════════════════════════════════════
        System.out.println("========== 复制以下代码到 MutCreativeTab.java ==========\n");

        // toolsTab
        System.out.println("if (event.getTabKey() == toolsTab) {");
        System.out.println("    // SHOVEL PICKAXE AXE HOE");
        for (String constName : MATERIALS) {
            System.out.println("    event.accept(MutModItems." + constName + "_SHOVEL.get());");
            System.out.println("    event.accept(MutModItems." + constName + "_PICKAXE.get());");
            System.out.println("    event.accept(MutModItems." + constName + "_AXE.get());");
            System.out.println("    event.accept(MutModItems." + constName + "_HOE.get());");
        }
        System.out.println("}\n");

        // combatTab
        System.out.println("if (event.getTabKey() == combatTab) {");

        // 剑和斧：第一条跟在龙斧后面，后续每个材质的剑跟在前一个材质的斧后面，斧跟在剑后面
        String prevAxe = null;
        for (int i = 0; i < MATERIALS.length; i++) {
            String c = "MutModItems." + MATERIALS[i];
            if (i == 0) {
                System.out.println("    event.insertAfter(new ItemStack(MutModItems.DRAGON_AXE.get()), " + c + "_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
            } else {
                System.out.println("    event.insertAfter(MutModItems." + MATERIALS[i-1] + "_AXE.get().getDefaultInstance(), " + c + "_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
            }
            System.out.println("    event.insertAfter(" + c + "_SWORD.get().getDefaultInstance(), " + c + "_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
        }

        System.out.println();
        for (int i = 0; i < MATERIALS.length; i++) {
            String c = "MutModItems." + MATERIALS[i];
            String after = (i == 0)
                    ? "new ItemStack(MutModItems.DRAGON_CHESTPLATE_ELYTRA.get())"
                    : "MutModItems." + MATERIALS[i-1] + "_BOOTS.get().getDefaultInstance()";
            System.out.println("    event.insertAfter(" + after + ", " + c + "_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
            System.out.println("    event.insertAfter(" + c + "_HELMET.get().getDefaultInstance(), " + c + "_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
            System.out.println("    event.insertAfter(" + c + "_CHESTPLATE.get().getDefaultInstance(), " + c + "_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
            System.out.println("    event.insertAfter(" + c + "_LEGGINGS.get().getDefaultInstance(), " + c + "_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);");
        }
        System.out.println("}\n");

        System.out.println("========== 代码结束 ==========");
    }

    // =====================================================================
    // 配方生成
    // =====================================================================

    private static int generateNormalRecipes(MatMeta m) {
        int count = 0;
        for (ItemType type : ITEM_TYPES) {
            String productId = "mut:" + m.name() + "_" + type.idSuffix();
            String fileName = m.name() + "_" + type.idSuffix() + ".json";

            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"type\": \"minecraft:crafting_shaped\",\n");
            json.append("  \"category\": \"").append(type.category()).append("\",\n");
            json.append("  \"pattern\": [\n");
            for (int i = 0; i < type.pattern().length; i++) {
                json.append("    \"").append(type.pattern()[i]).append("\"");
                if (i < type.pattern().length - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ],\n");
            json.append("  \"key\": {\n");
            if (type.isTwoSlot()) {
                json.append("    \"a\": {\n");
                json.append("      \"item\": \"").append(m.repairItemId()).append("\"\n");
                json.append("    }\n");
            } else {
                json.append("    \"a\": {\n");
                json.append("      \"item\": \"").append(m.repairItemId()).append("\"\n");
                json.append("    },\n");
                json.append("    \"b\": {\n");
                json.append("      \"item\": \"").append(STICK).append("\"\n");
                json.append("    }\n");
            }
            json.append("  },\n");
            json.append("  \"result\": {\n");
            json.append("    \"id\": \"").append(productId).append("\",\n");
            json.append("    \"count\": 1\n");
            json.append("  }\n");
            json.append("}\n");

            if (write(RECIPE_DIR + "/" + fileName, json.toString())) count++;
        }
        return count;
    }

    private static int generateSmithingRecipes(MatMeta m) {
        int count = 0;
        if (m.fromMaterials().isEmpty()) {
            System.out.println("  [跳过] " + m.name() + " 无升级来源配置");
            return 0;
        }

        for (String from : m.fromMaterials()) {
            for (ItemType type : ITEM_TYPES) {
                String baseId = "mut:" + from + "_" + type.idSuffix();
                String resultId = "mut:" + m.name() + "_" + type.idSuffix();
                String fileName = from + "_to_" + m.name() + "_" + type.idSuffix() + ".json";

                String json = """
                    {
                      "type": "minecraft:smithing_transform",
                      "template": {
                        "item": "%s"
                      },
                      "base": {
                        "item": "%s"
                      },
                      "addition": {
                        "item": "%s"
                      },
                      "result": {
                        "id": "%s"
                      }
                    }
                    """.formatted(m.template(), baseId, m.addition(), resultId);

                if (write(RECIPE_DIR + "/" + fileName, json)) count++;
            }
        }
        return count;
    }

    // =====================================================================
    // 材质元数据（内嵌，纯字符串）
    // =====================================================================

    private record MatMeta(
            String name,
            String craftingType,
            String repairItemId,
            List<String> fromMaterials,
            String template,
            String addition
    ) {}

    private static MatMeta loadMaterial(String constName) {
        return switch (constName) {
            case "AMETHYST" -> new MatMeta("amethyst", "NORMAL", "minecraft:amethyst_shard", List.of(), "", "");
            case "NETHERITE_AMETHYST" -> new MatMeta("netherite_amethyst","SMITHING","mut:netherite_amethyst_ingot", List.of("amethyst"), "mut:netherite_amethyst_upgrade_smithing_template", "mut:netherite_amethyst_ingot");
            // 新增材质在这里加
            case "WITHER" -> new MatMeta("wither", "SMITHING", "minecraft:nether_star", List.of("nether_star"), "mut:nether_star_upgrade_template", "minecraft:wither_skeleton_skull");
            case "SUPER_NETHERITE" -> new MatMeta("super_netherite","SMITHING","minecraft:netherite_ingot", List.of("netherite"), "minecraft:netherite__upgrade_smithing_template", "minecraft:netherite_ingot");
            case "LAPIS_LAZULI" -> new MatMeta("lapis_lazuli", "NORMAL", "minecraft:lapis_lazuli", List.of(), "", "");
            case "NETHERITE_LAPIS_LAZULI" -> new MatMeta("netherite_lapis_lazuli","SMITHING","mut:netherite_lapis_lazuli_ingot", List.of("lapis_lazuli"), "mut:netherite_lapis_lazuli_upgrade_smithing_template", "mut:netherite_lapis_lazuli_ingot");
            case "ECHOITE" -> new MatMeta("echoite", "SMITHING", "mut:echoite_ingot", List.of("netherite"), "mut:echoite_upgrade_smithing_template", "mut:echoite_ingot");
            case "THUNDER_COPPER" -> new MatMeta("thunder_copper", "SMITHING", "", List.of("netherite_copper"), "mut:thunder_upgrade_smithing_template", "mut:thunder_copper_star");
            case "FLAME_GOLD" -> new MatMeta("flame_gold", "SMITHING", "", List.of("gilding"), "mut:magma_upgrade_smithing_template", "mut:flame_gold_ingot");
            case "POSITION_STEEL" -> new MatMeta("position_steel", "SMITHING", "", List.of("steel"), "mut:poison_upgrade_smithing_template", "mut:position_steel_ingot");
            case "UNCANNY_AMETHYST" -> new MatMeta("uncanny_amethyst", "SMITHING", "", List.of("netherite_amethyst"), "mut:uncanny_amethyst_upgrade_smithing_template", "mut:uncanny_amethyst_star");

            default -> null;
            //case "X" -> new MatMeta("x", "NORMAL", "minecraft:x", List.of(), "", "");
        };
    }

    // =====================================================================
    // 辅助
    // =====================================================================

    private static boolean write(String path, String content) {
        try {
            Files.createDirectories(Paths.get(path).getParent());
            try (FileWriter w = new FileWriter(path)) { w.write(content); }
            System.out.println("  生成: " + path);
            return true;
        } catch (IOException ex) {
            System.err.println("  失败: " + path);
            return false;
        }
    }
}