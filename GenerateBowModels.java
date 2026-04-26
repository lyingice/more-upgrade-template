import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GenerateBowModels {

    // 你的所有弓的材料名称
    private static final String[] MATERIALS = {
            "obsidian","netherite_obsidian","crying_obsidian", "steel","advanced_steel","gilding","blue_diamond","nether_star"
    };

    // ========== 在这里自定义每个弓的拉动数值 ==========
    private static final Map<String, float[]> BOW_PULL_VALUES = new HashMap<>();
    static {
        BOW_PULL_VALUES.put("obsidian", new float[]{0.81f, 1.18f});
        BOW_PULL_VALUES.put("netherite_obsidian", new float[]{0.81f, 1.18f});
        BOW_PULL_VALUES.put("crying_obsidian", new float[]{0.81f, 1.18f});
        BOW_PULL_VALUES.put("steel", new float[]{0.65f, 0.95f});
        BOW_PULL_VALUES.put("advanced_steel", new float[]{0.48f, 0.71f});
        BOW_PULL_VALUES.put("gilding", new float[]{0.32f, 0.47f});
        BOW_PULL_VALUES.put("blue_diamond", new float[]{0.48f, 0.71f});
    }

    // 默认拉动数值
    private static final float DEFAULT_PULL_1 = 0.65f;
    private static final float DEFAULT_PULL_2 = 0.9f;

    // 输出目录
    private static final String OUTPUT_DIR = "src/main/resources/assets/mut/models/item";

    // 标准弓的display设置
    private static final String DISPLAY_SETTINGS =
            "  \"display\": {\n" +
                    "    \"thirdperson_righthand\": {\n" +
                    "      \"rotation\": [-80, 260, -40],\n" +
                    "      \"translation\": [-1, -2, 2.5],\n" +
                    "      \"scale\": [0.9, 0.9, 0.9]\n" +
                    "    },\n" +
                    "    \"thirdperson_lefthand\": {\n" +
                    "      \"rotation\": [-80, -280, 40],\n" +
                    "      \"translation\": [-1, -2, 2.5],\n" +
                    "      \"scale\": [0.9, 0.9, 0.9]\n" +
                    "    },\n" +
                    "    \"firstperson_righthand\": {\n" +
                    "      \"rotation\": [0, -90, 25],\n" +
                    "      \"translation\": [1.13, 3.2, 1.13],\n" +
                    "      \"scale\": [0.68, 0.68, 0.68]\n" +
                    "    },\n" +
                    "    \"firstperson_lefthand\": {\n" +
                    "      \"rotation\": [0, 90, -25],\n" +
                    "      \"translation\": [1.13, 3.2, 1.13],\n" +
                    "      \"scale\": [0.68, 0.68, 0.68]\n" +
                    "    }\n" +
                    "  },\n";

    public static void main(String[] args) {
        System.out.println("开始生成弓模型文件...");

        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            System.err.println("无法创建目录: " + OUTPUT_DIR);
            e.printStackTrace();
            return;
        }

        int totalFiles = 0;

        for (String material : MATERIALS) {
            float[] pullValues = BOW_PULL_VALUES.getOrDefault(material,
                    new float[]{DEFAULT_PULL_1, DEFAULT_PULL_2});
            float pull1 = pullValues[0];
            float pull2 = pullValues[1];

            System.out.println("生成 " + material + " 弓，拉动数值: " + pull1 + ", " + pull2);

            // 生成基础弓模型 (_bow)
            String bowName = material + "_bow";
            totalFiles += generateBaseBowModel(bowName, pull1, pull2, material);

            // 生成拉动状态的弓模型 (_bow_0, _bow_1, _bow_2)
            // 模型文件名: xxx_bow_0.json
            // 模型内纹理: xxx_bow_pulling_0
            String[] pullStateNames = {"_bow_0", "_bow_1", "_bow_2"};
            String[] pullTextureNames = {"_bow_pulling_0", "_bow_pulling_1", "_bow_pulling_2"};

            for (int i = 0; i < pullStateNames.length; i++) {
                String modelFileName = material + pullStateNames[i];
                String textureName = material + pullTextureNames[i];
                totalFiles += generatePullStateModel(modelFileName, textureName);
            }
        }

        System.out.println("生成完成！共生成 " + totalFiles + " 个 JSON 文件");
        System.out.println("输出目录: " + OUTPUT_DIR);
    }

    private static int generateBaseBowModel(String modelName, float pull1, float pull2, String material) {
        String filePath = OUTPUT_DIR + "/" + modelName + ".json";
        String content = createBaseBowModel(modelName, pull1, pull2, material);

        if (writeFile(filePath, content)) {
            return 1;
        }
        return 0;
    }

    private static int generatePullStateModel(String modelFileName, String textureName) {
        String filePath = OUTPUT_DIR + "/" + modelFileName + ".json";
        String content = createPullStateModel(modelFileName, textureName);

        if (writeFile(filePath, content)) {
            return 1;
        }
        return 0;
    }

    // 创建基础弓模型 - 使用自定义 predicate
    private static String createBaseBowModel(String modelName, float pull1, float pull2, String material) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"parent\": \"item/generated\",\n");
        sb.append("  \"textures\": {\n");
        sb.append("    \"layer0\": \"mut:item/").append(modelName).append("\"\n");
        sb.append("  },\n");

        // 添加display设置
        sb.append(DISPLAY_SETTINGS);

        // 添加 overrides - 使用自定义的 predicate 名称
        sb.append("  \"overrides\": [\n");
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"mut:").append(material).append("_bow_pulling\": 1\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"mut:item/").append(material).append("_bow_0\"\n");
        sb.append("    },\n");
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"mut:").append(material).append("_bow_pulling\": 1,\n");
        sb.append("        \"mut:").append(material).append("_bow_pull\": ").append(pull1).append("\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"mut:item/").append(material).append("_bow_1\"\n");
        sb.append("    },\n");
        sb.append("    {\n");
        sb.append("      \"predicate\": {\n");
        sb.append("        \"mut:").append(material).append("_bow_pulling\": 1,\n");
        sb.append("        \"mut:").append(material).append("_bow_pull\": ").append(pull2).append("\n");
        sb.append("      },\n");
        sb.append("      \"model\": \"mut:item/").append(material).append("_bow_2\"\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    // 创建拉动状态模型
    private static String createPullStateModel(String modelFileName, String textureName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"parent\": \"item/generated\",\n");
        sb.append("  \"textures\": {\n");
        sb.append("    \"layer0\": \"mut:item/").append(textureName).append("\"\n");
        sb.append("  },\n");
        sb.append(DISPLAY_SETTINGS);
        // 去掉最后一个逗号
        String content = sb.toString();
        content = content.substring(0, content.length() - 2) + "\n";
        content += "}\n";
        return content;
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