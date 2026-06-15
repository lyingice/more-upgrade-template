import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerateSpearModels {

    private static final String[] MATERIALS = {
            "wooden","stone","copper", "iron", "golden", "diamond", "netherite","advanced_steel","amethyst","blue_diamond",
            "crying_obsidian","dragon","emerald","gilding",
            "nether_star","netherite_amethyst","netherite_copper",
            "netherite_emerald","netherite_obsidian","netherite_redstone",
            "obsidian","steel","wither",
            "lapis_lazuli","netherite_lapis_lazuli","echoite","poison_steel","flame_gold","thunder_copper","uncanny_amethyst"
    };
    private static final String MOD_ID = "mut";
    private static final String MODELS_DIR = "src/main/resources/assets/" + MOD_ID + "/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/" + MOD_ID + "/tags/item";

    public static void main(String[] args) {
        System.out.println("开始生成矛模型文件...");
        try {
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int totalFiles = 0;
        for (String material : MATERIALS) {
            totalFiles += generateInHandModel(material);
            totalFiles += generateBaseModel(material);
        }
        totalFiles += generateTagFile();
        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
    }

    // ==================== _in_hand 模型 ====================

    private static int generateInHandModel(String material) {
        String path = MODELS_DIR + "/" + material + "_spear_in_hand.json";
        String content = "{\n" +
                "  \"parent\": \"item/generated\",\n" +
                "  \"textures\": {\n" +
                "    \"layer0\": \"" + MOD_ID + ":item/" + material + "_spear_in_hand\"\n" +
                "  },\n" +
                "  \"display\": {\n" +
                "    \"firstperson_righthand\": {\n" +
                "      \"rotation\": [ -20, 90, -35 ],\n" +
                "      \"translation\": [ 3.13, 2.0, 0.13],\n" +
                "      \"scale\": [ 1.36, 1.36, 0.68 ]\n" +
                "    },\n" +
                "    \"firstperson_lefthand\": {\n" +
                "      \"rotation\": [ -20, -90, 35 ],\n" +
                "      \"translation\": [ 3.13, 2.0, 0.13],\n" +
                "      \"scale\": [ 1.36, 1.36, 0.68 ]\n" +
                "    },\n" +
                "    \"thirdperson_righthand\": {\n" +
                "      \"rotation\": [ 5, 270, -40 ],\n" +
                "      \"translation\": [ 0, 2, 2 ],\n" +
                "      \"scale\": [1.7, 1.7, 0.85 ]\n" +
                "    },\n" +
                "    \"thirdperson_lefthand\": {\n" +
                "      \"rotation\": [ 5, -270, 40 ],\n" +
                "      \"translation\": [ 0, 2, 2 ],\n" +
                "      \"scale\": [1.7, 1.7, 0.85 ]\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        return writeFile(path, content) ? 1 : 0;
    }

    // ==================== 基础模型（separate_transforms） ====================

    private static int generateBaseModel(String material) {
        String path = MODELS_DIR + "/" + material + "_spear.json";
        String content = "{\n" +
                "  \"loader\": \"neoforge:separate_transforms\",\n" +
                "  \"gui_light\": \"front\",\n" +
                "  \"base\": {\n" +
                "    \"parent\": \"" + MOD_ID + ":item/" + material + "_spear_in_hand\",\n" +
                "    \"textures\": {\n" +
                "      \"layer0\": \"" + MOD_ID + ":item/" + material + "_spear_in_hand\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"perspectives\": {\n" +
                "    \"gui\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_spear\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"fixed\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_spear\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"ground\": {\n" +
                "      \"parent\": \"item/generated\",\n" +
                "      \"textures\": {\n" +
                "        \"layer0\": \"" + MOD_ID + ":item/" + material + "_spear\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        return writeFile(path, content) ? 1 : 0;
    }

    // ==================== 标签 ====================

    private static int generateTagFile() {
        String path = TAGS_DIR + "/spear.json";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"values\": [\n");
        for (int i = 0; i < MATERIALS.length; i++) {
            sb.append("    \"").append(MOD_ID).append(":").append(MATERIALS[i]).append("_spear\"");
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