import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerateModels {

    // 你的所有盔甲材料名称
    private static final String[] MATERIALS = {
            "obsidian", "steel", "dragon", "blue_diamond", "gilding", "netherite_obsidian", "crying_obsidian", "copper", "emerald_armor", "advanced_steel", "netherite_redstone","nether_star","golden_chain","diamond_chain","netherite_chain"
            // 在这里添加你其他的17套盔甲材料名称
    };

    // 盔甲部件
    private static final String[] PARTS = {"helmet", "chestplate", "leggings", "boots"};

    // 饰纹材料及对应的 trim_type 值（从0.1开始到1.0）
    private static final String[] TRIM_MATERIALS = {
            "quartz", "iron", "netherite", "redstone", "copper",
            "gold", "emerald", "diamond", "lapis", "amethyst"
    };

    private static final float[] TRIM_VALUES = {
            0.1f, 0.2f, 0.3f, 0.4f, 0.5f,
            0.6f, 0.7f, 0.8f, 0.9f, 1.0f
    };

    // 输出目录
    private static final String OUTPUT_DIR = "src/main/resources/assets/mut/models/item";

    public static void main(String[] args) {
        System.out.println("开始生成盔甲模型文件...");

        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            System.err.println("无法创建目录: " + OUTPUT_DIR);
            e.printStackTrace();
            return;
        }

        int totalFiles = 0;

        for (String material : MATERIALS) {
            for (String part : PARTS) {
                String armorName = material + "_" + part;
                totalFiles += generateArmorModel(armorName);
            }
        }

        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
        System.out.println("输出目录: " + OUTPUT_DIR);
    }

    private static int generateArmorModel(String armorName) {
        int count = 0;

        // 生成基础模型文件（包含 overrides）
        String baseModelPath = OUTPUT_DIR + "/" + armorName + ".json";
        if (writeFile(baseModelPath, createBaseModel(armorName))) {
            count++;
        }

        // 为每种饰纹生成单独的模型文件
        for (String trimMaterial : TRIM_MATERIALS) {
            String trimmedModelPath = OUTPUT_DIR + "/" + armorName + "_" + trimMaterial + "_trim.json";
            if (writeFile(trimmedModelPath, createTrimModel(armorName, trimMaterial))) {
                count++;
            }
        }

        return count;
    }

    private static String createBaseModel(String armorName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"parent\": \"minecraft:item/generated\",\n");
        sb.append("  \"textures\": {\n");
        sb.append("    \"layer0\": \"mut:item/").append(armorName).append("\"\n");
        sb.append("  },\n");
        sb.append("  \"overrides\": [\n");

        for (int i = 0; i < TRIM_MATERIALS.length; i++) {
            String trimMaterial = TRIM_MATERIALS[i];
            float trimValue = TRIM_VALUES[i];
            sb.append("    {\n");
            sb.append("      \"model\": \"mut:item/").append(armorName).append("_").append(trimMaterial).append("_trim\",\n");
            sb.append("      \"predicate\": {\n");
            sb.append("        \"trim_type\": ").append(trimValue).append("\n");
            sb.append("      }\n");
            sb.append("    }");
            if (i < TRIM_MATERIALS.length - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private static String createTrimModel(String armorName, String trimMaterial) {
        String armorType = getArmorType(armorName);

        return "{\n" +
                "  \"parent\": \"minecraft:item/generated\",\n" +
                "  \"textures\": {\n" +
                "    \"layer0\": \"mut:item/" + armorName + "\",\n" +
                "    \"layer1\": \"minecraft:trims/items/" + armorType + "_trim_" + trimMaterial + "\"\n" +
                "  }\n" +
                "}\n";
    }

    private static String getArmorType(String armorName) {
        if (armorName.contains("helmet")) return "helmet";
        if (armorName.contains("chestplate")) return "chestplate";
        if (armorName.contains("leggings")) return "leggings";
        if (armorName.contains("boots")) return "boots";
        return "helmet";
    }

    private static boolean writeFile(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            System.out.println("生成: " + path);
            return true;
        } catch (IOException e) {
            System.err.println("写入失败: " + path);
            e.printStackTrace();
            return false;
        }
    }
}