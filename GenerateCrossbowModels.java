import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 弩模型生成器（1.21.1 兼容版）
 * 使用原版弩的 overrides 谓词格式
 *
 * 生成：
 * 1. 基础模型 JSON（含 overrides）→ assets/模组/models/item/xxx_crossbow.json
 * 2. 子模型 JSON              → assets/模组/models/item/xxx_crossbow_xxx.json
 * 3. 标签 JSON                → data/模组/tags/item/crossbow.json
 */
public class GenerateCrossbowModels {

    private static final String[] MATERIALS = {
            "iron", "diamond","golden","netherite"
    };

    private static final Map<String, float[]> PULL_THRESHOLDS = new LinkedHashMap<>();
    static {
        // PULL_THRESHOLDS.put("diamond", new float[]{0.5f, 0.9f});
    }

    private static final float DEFAULT_PULL_THRESHOLD_1 = 0.58f;
    private static final float DEFAULT_PULL_THRESHOLD_2 = 1.0f;
    private static final String MOD_ID = "mut";

    private static final String MODELS_DIR = "src/main/resources/assets/" + MOD_ID + "/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/" + MOD_ID + "/tags/item";

    private static final String DISPLAY_SETTINGS =
            "  \"display\": {\n" +
                    "    \"thirdperson_righthand\": {\n" +
                    "      \"rotation\": [-90, 0, -60],\n" +
                    "      \"translation\": [2, 0.1, -3],\n" +
                    "      \"scale\": [0.9, 0.9, 0.9]\n" +
                    "    },\n" +
                    "    \"thirdperson_lefthand\": {\n" +
                    "      \"rotation\": [-90, 0, 30],\n" +
                    "      \"translation\": [2, 0.1, -3],\n" +
                    "      \"scale\": [0.9, 0.9, 0.9]\n" +
                    "    },\n" +
                    "    \"firstperson_righthand\": {\n" +
                    "      \"rotation\": [-90, 0, -55],\n" +
                    "      \"translation\": [1.13, 3.2, 1.13],\n" +
                    "      \"scale\": [0.68, 0.68, 0.68]\n" +
                    "    },\n" +
                    "    \"firstperson_lefthand\": {\n" +
                    "      \"rotation\": [-90, 0, 35],\n" +
                    "      \"translation\": [1.13, 3.2, 1.13],\n" +
                    "      \"scale\": [0.68, 0.68, 0.68]\n" +
                    "    }\n" +
                    "  }";

    private static final String[] SUB_MODEL_TYPES = {
            "arrow", "firework",
            "pulling_0", "pulling_1", "pulling_2"
    };

    public static void main(String[] args) {
        System.out.println("开始生成弩模型文件（1.21.1 overrides 系统）...");

        try {
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException e) {
            System.err.println("无法创建目录");
            e.printStackTrace();
            return;
        }

        int totalFiles = 0;

        for (String material : MATERIALS) {
            float[] thresholds = PULL_THRESHOLDS.getOrDefault(material,
                    new float[]{DEFAULT_PULL_THRESHOLD_1, DEFAULT_PULL_THRESHOLD_2});
            float t1 = thresholds[0];
            float t2 = thresholds[1];

            System.out.println("生成 " + material + " 弩，拉动阈值: " + t1 + ", " + t2);

            totalFiles += generateBaseModel(material, t1, t2);

            for (String subType : SUB_MODEL_TYPES) {
                totalFiles += generateSubModel(material, subType);
            }
        }

        // 生成标签文件
        totalFiles += generateTagFile();

        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
        System.out.println("模型目录: " + MODELS_DIR);
        System.out.println("标签目录: " + TAGS_DIR);
    }

    // ==================== 基础模型（standby 纹理 + overrides） ====================

    private static int generateBaseModel(String material, float t1, float t2) {
        String fileName = material + "_crossbow";
        String filePath = MODELS_DIR + "/" + fileName + ".json";
        String content = createBaseModel(material, t1, t2);
        return writeFile(filePath, content) ? 1 : 0;
    }

    private static String createBaseModel(String material, float t1, float t2) {
        String base = MOD_ID + ":item/" + material + "_crossbow";

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"parent\": \"item/generated\",\n");
        sb.append("  \"textures\": {\n");
        sb.append("    \"layer0\": \"").append(base).append("_standby\"\n");
        sb.append("  },\n");
        sb.append(DISPLAY_SETTINGS);
        // 关键：display 后面加逗号
        sb.append(",\n");
        sb.append("  \"overrides\": [\n");

        // pulling_0
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"pulling\": 1\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"").append(base).append("_pulling_0\"\n");
        sb.append("    },\n");

        // pulling_1
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"pulling\": 1,\n");
        sb.append("        \"pull\": ").append(t1).append("\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"").append(base).append("_pulling_1\"\n");
        sb.append("    },\n");

        // pulling_2
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"pulling\": 1,\n");
        sb.append("        \"pull\": ").append(t2).append("\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"").append(base).append("_pulling_2\"\n");
        sb.append("    },\n");

        // charged
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"charged\": 1\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"").append(base).append("_arrow\"\n");
        sb.append("    },\n");

        // charged + firework
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"charged\": 1,\n");
        sb.append("        \"firework\": 1\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"").append(base).append("_firework\"\n");
        sb.append("    }\n");

        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    // ==================== 子模型 ====================

    private static int generateSubModel(String material, String subType) {
        String fileName = material + "_crossbow_" + subType;
        String filePath = MODELS_DIR + "/" + fileName + ".json";
        String content = createSubModel(material, subType);
        return writeFile(filePath, content) ? 1 : 0;
    }

    private static String createSubModel(String material, String subType) {
        String textureName = material + "_crossbow_" + subType;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"parent\": \"item/generated\",\n");
        sb.append("  \"textures\": {\n");
        sb.append("    \"layer0\": \"").append(MOD_ID).append(":item/").append(textureName).append("\"\n");
        sb.append("  },\n");
        sb.append(DISPLAY_SETTINGS);
        sb.append("\n}\n");

        return sb.toString();
    }

    // ==================== 标签文件 ====================

    private static int generateTagFile() {
        String filePath = TAGS_DIR + "/crossbow.json";
        String content = createTagFile();
        return writeFile(filePath, content) ? 1 : 0;
    }

    private static String createTagFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"values\": [\n");

        for (int i = 0; i < MATERIALS.length; i++) {
            sb.append("    \"").append(MOD_ID).append(":").append(MATERIALS[i]).append("_crossbow\"");
            if (i < MATERIALS.length - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    // ==================== 工具方法 ====================

    private static boolean writeFile(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            System.out.println("  生成: " + path);
            return true;
        } catch (IOException e) {
            System.err.println("  写入失败: " + path);
            e.printStackTrace();
            return false;
        }
    }
}