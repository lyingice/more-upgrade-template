import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 三叉戟模型生成器 — NeoForge separate_transforms 格式
 * 生成：
 * - xxx_trident.json          → 基础模型（手持 3D + GUI 2D + throwing overrides）
 * - xxx_trident_throwing.json → 投掷蓄力模型
 * - trident.json              → #mut:trident 标签
 */
public class GenerateTridentModels {

    private static final String[] MATERIALS = {
            "wooden", "copper", "iron", "golden", "diamond", "netherite",
            "steel", "gilding", "blue_diamond", "advanced_steel",
            "obsidian", "netherite_obsidian", "crying_obsidian",
            "nether_star", "dragon", "wither","netherite_redstone",
            "netherite_copper","netherite_emerald","netherite_amethyst",
            "amethyst", "emerald"
    };
    private static final String MOD_ID = "mut";
    private static final String MODELS_DIR = "src/main/resources/assets/" + MOD_ID + "/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/" + MOD_ID + "/tags/item";

    public static void main(String[] args) {
        System.out.println("开始生成三叉戟模型文件（separate_transforms 格式）...");
        try {
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int totalFiles = 0;
        for (String material : MATERIALS) {
            totalFiles += generateBaseModel(material);
            totalFiles += generateThrowingModel(material);
        }
        totalFiles += generateTagFile();
        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
    }

    // ==================== 基础模型（手持 3D + GUI 2D + overrides） ====================

    private static int generateBaseModel(String material) {
        String path = MODELS_DIR + "/" + material + "_trident.json";
        String content = "{\n" +
                "  \"loader\": \"neoforge:separate_transforms\",\n" +
                "  \"gui_light\": \"front\",\n" +
                "  \"base\": {\n" +
                "    \"parent\": \"" + MOD_ID + ":custom/trident\",\n" +
                "    \"textures\": {\n" +
                "      \"2\": \"" + MOD_ID + ":block/" + material + "_trident\",\n" +
                "      \"particle\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"overrides\": [\n" +
                "    {\n" +
                "      \"predicate\": {\n" +
                "        \"throwing\": 1\n" +
                "      },\n" +
                "      \"model\": \"" + MOD_ID + ":item/" + material + "_trident_throwing\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"perspectives\": {\n" +
                "    \"gui\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"fixed\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"ground\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        return writeFile(path, content) ? 1 : 0;
    }

    // ==================== 投掷蓄力模型 ====================

    private static int generateThrowingModel(String material) {
        String path = MODELS_DIR + "/" + material + "_trident_throwing.json";
        String content = "{\n" +
                "  \"loader\": \"neoforge:separate_transforms\",\n" +
                "  \"gui_light\": \"front\",\n" +
                "  \"base\": {\n" +
                "    \"parent\": \"" + MOD_ID + ":custom/trident\",\n" +
                "    \"textures\": {\n" +
                "      \"2\": \"" + MOD_ID + ":block/" + material + "_trident\",\n" +
                "      \"particle\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "      \"thirdperson_righthand\": {\n" +
                "        \"rotation\": [-180, 90, 0],\n" +
                "        \"translation\": [0, 4, 1],\n" +
                "        \"scale\": [1, 1, 1]\n" +
                "      },\n" +
                "      \"thirdperson_lefthand\": {\n" +
                "        \"rotation\": [-180, 90, 0],\n" +
                "        \"translation\": [0, 4, 1],\n" +
                "        \"scale\": [1, 1, 1]\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"perspectives\": {\n" +
                "    \"gui\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"fixed\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"ground\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_trident\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        return writeFile(path, content) ? 1 : 0;
    }

    // ==================== 标签 ====================

    private static int generateTagFile() {
        String path = TAGS_DIR + "/trident.json";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"values\": [\n");
        for (int i = 0; i < MATERIALS.length; i++) {
            sb.append("    \"").append(MOD_ID).append(":").append(MATERIALS[i]).append("_trident\"");
            if (i < MATERIALS.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return writeFile(path, sb.toString()) ? 1 : 0;
    }

    // ==================== 工具 ====================

    private static boolean writeFile(String path, String content) {
        try (FileWriter w = new FileWriter(path)) {
            w.write(content);
            System.out.println("  生成: " + path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}