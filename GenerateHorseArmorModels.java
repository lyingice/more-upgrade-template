import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 马铠模型生成器
 * 生成：
 * - {材质}_horse_armor.json → 基础模型（马铠的 display 配置）
 * - horse_armor2.json → #mut:horse_armor2 标签
 */
public class GenerateHorseArmorModels {

    // ========== 在这里填写你需要的材质名称 ==========
    private static final String[] MATERIALS = {
            "steel", "advanced_steel", "gilding", "blue_diamond",
            "obsidian", "netherite_obsidian", "crying_obsidian",
            "nether_star", "dragon", "wither",
            "netherite_copper", "netherite_redstone", "netherite_emerald", "netherite_amethyst",
            "emerald", "amethyst","lapis_lazuli","netherite_lapis_lazuli"
    };
    // =============================================

    private static final String MOD_ID = "mut";
    private static final String MODELS_DIR = "src/main/resources/assets/" + MOD_ID + "/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/" + MOD_ID + "/tags/item";

    public static void main(String[] args) {
        System.out.println("开始生成马铠模型文件...");
        try {
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int totalFiles = 0;
        for (String material : MATERIALS) {
            totalFiles += generateHorseArmorModel(material);
        }
        totalFiles += generateTagFile();

        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
    }

    /**
     * 生成马铠基础模型
     */
    private static int generateHorseArmorModel(String material) {
        String path = MODELS_DIR + "/" + material + "_horse_armor.json";
        String content = String.format("""
            {
              "parent": "item/generated",
              "textures": {
                "layer0": "%s:item/%s_horse_armor"
              }
            }
            """, MOD_ID, material);
        return writeFile(path, content) ? 1 : 0;
    }

    /**
     * 生成马铠标签文件 #mut:horse_armor2
     */
    private static int generateTagFile() {
        String path = TAGS_DIR + "/horse_armor2.json";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"values\": [\n");
        for (int i = 0; i < MATERIALS.length; i++) {
            sb.append("    \"").append(MOD_ID).append(":").append(MATERIALS[i]).append("_horse_armor\"");
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