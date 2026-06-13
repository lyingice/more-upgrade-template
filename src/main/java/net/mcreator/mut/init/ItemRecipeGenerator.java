package net.mcreator.mut.init;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 特殊物品配方生成器
 * 支持锻造台配方 + 工作台配方
 */
public class ItemRecipeGenerator {

    private static final String RECIPE_DIR = "src/main/resources/data/mut/recipe";

    // ========== 配方组合配置 ==========
    // 格式：物品类型 -> 目标材质列表
    private static final Map<String, List<String>> RECIPES_TO_GENERATE = new LinkedHashMap<>();

    static {
        // 三叉戟
        RECIPES_TO_GENERATE.put("trident", Arrays.asList("netherite_lapis_lazuli","poison_steel",
                "flame_gold","uncanny_amethyst","thunder_copper"
        ));

        // 重锤
        RECIPES_TO_GENERATE.put("mace", Arrays.asList("netherite_lapis_lazuli","poison_steel",
                "flame_gold","uncanny_amethyst","thunder_copper"
        ));

        // 弓
        RECIPES_TO_GENERATE.put("bow", Arrays.asList("netherite_lapis_lazuli","poison_steel",
                "flame_gold","uncanny_amethyst","thunder_copper"
        ));

        // 弩
        RECIPES_TO_GENERATE.put("crossbow", Arrays.asList("netherite_lapis_lazuli","poison_steel",
                "flame_gold","uncanny_amethyst","thunder_copper"
        ));

        // 盾牌
        RECIPES_TO_GENERATE.put("shield", Arrays.asList("netherite_lapis_lazuli","poison_steel",
                "flame_gold","uncanny_amethyst","thunder_copper"
        ));

        // 工作台配方物品类型（这些物品的工作台配方会由 BASE_MATERIALS 中的材料生成）
        RECIPES_TO_GENERATE.put("spear", Arrays.asList("netherite_lapis_lazuli","poison_steel",
                "flame_gold","uncanny_amethyst","thunder_copper"));
        RECIPES_TO_GENERATE.put("horse_armor", Arrays.asList("netherite_lapis_lazuli"));
        RECIPES_TO_GENERATE.put("wolf_armor", Arrays.asList("netherite_lapis_lazuli"));
    }
    // ===========================

    public static void main(String[] args) {
        System.out.println("=== ItemRecipeGenerator Start Generate ===\n");

        try {
            Files.createDirectories(Paths.get(RECIPE_DIR));
        } catch (IOException ignored) {}

        int smithingCount = 0;
        int shapedCount = 0;

        for (Map.Entry<String, List<String>> entry : RECIPES_TO_GENERATE.entrySet()) {
            String itemType = entry.getKey();
            List<String> targetMaterials = entry.getValue();

            for (String targetMaterial : targetMaterials) {
                // 判断是否使用工作台配方（基础材料）
                if (MutBaseRecipeConfig.BASE_MATERIALS.contains(targetMaterial)) {
                    // 生成工作台配方
                    MutBaseRecipeConfig.ItemTypeTemplate template = MutBaseRecipeConfig.getTemplate(itemType);
                    if (template != null) {
                        String fileName = targetMaterial + "_" + itemType + ".json";
                        String filePath = RECIPE_DIR + "/" + fileName;

                        Map<Character, String> keys = MutBaseRecipeConfig.getKeys(targetMaterial, itemType);
                        String json = buildShapedRecipeJson(targetMaterial, itemType, template, keys);

                        if (write(filePath, json)) {
                            shapedCount++;
                        }
                    }
                } else {
                    // 生成锻造台配方
                    MutUpgradeRecipeConfig.UpgradeGroup targetConfig = getConfigForMaterial(targetMaterial);
                    if (targetConfig == null) {
                        System.err.println("  error: cannot find config for " + targetMaterial);
                        continue;
                    }

                    for (String fromMaterial : targetConfig.fromMaterials()) {
                        String fileName = fromMaterial + "_" + itemType + "_to_" + targetMaterial + "_" + itemType + ".json";
                        String filePath = RECIPE_DIR + "/" + fileName;

                        String baseId = "mut:" + fromMaterial + "_" + itemType;
                        String resultId = "mut:" + targetMaterial + "_" + itemType;

                        String json = buildSmithingRecipeJson(targetConfig.template(), baseId, targetConfig.addition(), resultId);

                        if (write(filePath, json)) {
                            smithingCount++;
                        }
                    }
                }
            }
        }

        System.out.println("\n=== Good！锻造台配方: " + smithingCount + " 个，工作台配方: " + shapedCount + " 个 ===\n");
    }

    private static MutUpgradeRecipeConfig.UpgradeGroup getConfigForMaterial(String material) {
        return MutUpgradeRecipeConfig.getAll().stream()
                .filter(group -> group.targetMaterial().equals(material))
                .findFirst()
                .orElse(null);
    }

    private static String buildSmithingRecipeJson(String template, String baseId, String addition, String resultId) {
        return """
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
            """.formatted(template, baseId, addition, resultId);
    }

    private static String buildShapedRecipeJson(String material, String itemType,
                                                MutBaseRecipeConfig.ItemTypeTemplate template, Map<Character, String> keys) {

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"type\": \"minecraft:crafting_shaped\",\n");
        json.append("  \"category\": \"equipment\",\n");
        json.append("  \"pattern\": [\n");

        String[] pattern = template.pattern();
        for (int i = 0; i < pattern.length; i++) {
            json.append("    \"").append(pattern[i]).append("\"");
            if (i < pattern.length - 1) json.append(",");
            json.append("\n");
        }

        json.append("  ],\n");
        json.append("  \"key\": {\n");

        int keyCount = 0;
        for (Map.Entry<Character, String> entry : keys.entrySet()) {
            keyCount++;
            json.append("    \"").append(entry.getKey()).append("\": {\n");
            json.append("      \"item\": \"").append(entry.getValue()).append("\"\n");
            json.append("    }");
            if (keyCount < keys.size()) json.append(",");
            json.append("\n");
        }

        json.append("  },\n");
        json.append("  \"result\": {\n");
        json.append("    \"id\": \"mut:").append(material).append("_").append(itemType).append("\",\n");
        json.append("    \"count\": 1\n");
        json.append("  }\n");
        json.append("}\n");

        return json.toString();
    }

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