import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 重锤模型生成器
 * 生成：
 * - {材质}_mace.json → 基础模型（包含手持/第一人称/第三人称的 display 配置）
 * - mace.json → #mut:mace 标签
 */
public class GenerateMaceModels {

    // ========== 在这里填写你需要的材质名称 ==========
    private static final String[] MATERIALS = {
            "wooden", "copper", "iron", "golden", "diamond", "netherite",
            "steel", "gilding", "blue_diamond", "advanced_steel","obsidian","netherite_obsidian","crying_obsidian",
            "nether_star","wither","dragon","netherite_redstone", "netherite_copper","netherite_emerald",
            "netherite_amethyst","amethyst", "emerald",
            "lapis_lazuli","netherite_lapis_lazuli","echoite","poison_steel","flame_gold","thunder_copper","uncanny_amethyst"
    };
    // =============================================

    private static final String MOD_ID = "mut";
    private static final String MODELS_DIR = "src/main/resources/assets/" + MOD_ID + "/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/" + MOD_ID + "/tags/item";

    public static void main(String[] args) {
        System.out.println("开始生成重锤模型文件...");
        try {
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int totalFiles = 0;
        for (String material : MATERIALS) {
            totalFiles += generateMaceModel(material);
        }
        totalFiles += generateTagFile();

        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
    }

    /**
     * 生成重锤基础模型
     */
    private static int generateMaceModel(String material) {
        String path = MODELS_DIR + "/" + material + "_mace.json";
        String content = String.format("""
            {
              "parent": "item/handheld",
              "textures": {
                "layer0": "%s:item/%s_mace"
              },
              "display": {
                "thirdperson_righthand": {
                  "rotation": [0, -90, 55],
                  "translation": [0, 4.0, 1],
                  "scale": [1, 1, 1]
                },
                "thirdperson_lefthand": {
                  "rotation": [0, 90, -55],
                  "translation": [0, 4.0, 1],
                  "scale": [1, 1, 1]
                },
                "firstperson_righthand": {
                  "rotation": [0, -90, 25],
                  "translation": [0, 3, 0.8],
                  "scale": [0.9, 0.9, 0.9]
                },
                "firstperson_lefthand": {
                  "rotation": [0, 90, -25],
                  "translation": [0, 3, 0.8],
                  "scale": [0.9, 0.9, 0.9]
                }
              }
            }
            """, MOD_ID, material);
        return writeFile(path, content) ? 1 : 0;
    }

    /**
     * 生成重锤标签文件 #mut:mace
     */
    private static int generateTagFile() {
        String path = TAGS_DIR + "/mace.json";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"values\": [\n");
        for (int i = 0; i < MATERIALS.length; i++) {
            sb.append("    \"").append(MOD_ID).append(":").append(MATERIALS[i]).append("_mace\"");
            if (i < MATERIALS.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return writeFile(path, sb.toString()) ? 1 : 0;
    }

    /**
     * 写入文件
     */
    private static boolean writeFile(String path, String content) {
        try (FileWriter w = new FileWriter(path)) {
            w.write(content);
            System.out.println("  生成: " + path);
            return true;
        } catch (IOException e) {
            System.err.println("  失败: " + path + " - " + e.getMessage());
            return false;
        }
    }
}