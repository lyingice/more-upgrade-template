package net.mcreator.mut.init;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MutToolsArmorCreator {

    private static final String ITEM_DIR = "src/main/java/net/mcreator/mut/item";
    private static final String MODELS_DIR = "src/main/resources/assets/mut/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/mut/tags/item";

    private static final String[] TOOL_TYPES = {"sword", "shovel", "pickaxe", "axe", "hoe"};
    private static final String[] ARMOR_TYPES = {"helmet", "chestplate", "leggings", "boots"};

    // ========== 配置区域 ==========
    // 要生成 Java 文件的材质列表
    private static final String[] MATERIALS_TO_GENERATE_JAVA = {
            // 在这里添加要生成 Java 文件的材质名

    };

    // 要生成标签文件的材质列表（可以单独控制）
    private static final String[] MATERIALS_TO_GENERATE_TAGS = {
            "amethyst", "netherite_amethyst",
            "wither","super_netherite"
    };

    // 是否生成 Java 文件（工具类、盔甲类、模型文件）
    private static final boolean GENERATE_JAVA = false;

    // 是否生成标签文件（tags）
    private static final boolean GENERATE_TAGS = true;
    // ===========================

    public static void main(String[] args) {
        System.out.println("=== MutToolsArmorCreator 开始生成 ===\n");

        if (GENERATE_JAVA) {
            generateJavaFiles();
        } else {
            System.out.println("跳过 Java 文件生成 (GENERATE_JAVA = false)");
        }

        if (GENERATE_TAGS) {
            generateTagFiles();
        } else {
            System.out.println("跳过标签文件生成 (GENERATE_TAGS = false)");
        }

        System.out.println("\n=== 生成完成 ===");
    }

    private static void generateJavaFiles() {
        System.out.println("--- 生成 Java 文件 ---");

        // 从 MutMaterials 获取要生成的材质
        List<MutMaterials.MutMaterial> materialsToGenerate = new ArrayList<>();
        for (MutMaterials.MutMaterial mat : MutMaterials.getAll()) {
            // 只生成在 MATERIALS_TO_GENERATE_JAVA 中指定的材质
            for (String target : MATERIALS_TO_GENERATE_JAVA) {
                if (mat.name.equals(target)) {
                    materialsToGenerate.add(mat);
                    break;
                }
            }
        }

        if (materialsToGenerate.isEmpty()) {
            System.out.println("没有要生成的材质，请配置 MATERIALS_TO_GENERATE_JAVA");
            return;
        }

        System.out.println("将要生成以下材质的 Java 文件:");
        for (MutMaterials.MutMaterial mat : materialsToGenerate) {
            System.out.println("  - " + mat.name + " (工具:" + mat.hasTools + ", 盔甲:" + mat.hasArmor + ")");
        }
        System.out.println();

        try {
            Files.createDirectories(Paths.get(ITEM_DIR));
            Files.createDirectories(Paths.get(MODELS_DIR));
        } catch (IOException ignored) {}

        int count = 0;

        for (MutMaterials.MutMaterial mat : materialsToGenerate) {
            String name = mat.name;
            String cn = capitalize(name);
            String upperName = name.toUpperCase();

            // 根据 hasTools 决定是否生成工具类
            if (mat.hasTools) {
                if (write(ITEM_DIR + "/" + cn + "Tools.java", buildTools(name, cn, upperName))) count++;
                // 生成工具模型
                for (String tool : TOOL_TYPES) {
                    String modelPath = MODELS_DIR + "/" + name + "_" + tool + ".json";
                    if (write(modelPath, buildToolModel(name, tool))) count++;
                }
            }

            // 根据 hasArmor 决定是否生成盔甲类
            if (mat.hasArmor) {
                if (write(ITEM_DIR + "/" + cn + "Armor.java", buildArmor(name, cn, upperName))) count++;
            }
        }

        System.out.println("Java 文件生成完成！共生成 " + count + " 个文件\n");
        printRegistrationCode(materialsToGenerate);
    }

    private static void generateTagFiles() {
        System.out.println("--- 生成标签文件 ---");

        List<String> toGenerate = Arrays.asList(MATERIALS_TO_GENERATE_TAGS);
        if (toGenerate.isEmpty() || (toGenerate.size() == 1 && toGenerate.get(0).equals("无"))) {
            System.out.println("没有要生成的标签，请配置 MATERIALS_TO_GENERATE_TAGS");
            return;
        }

        System.out.println("将要生成以下材质的标签:");
        for (String name : toGenerate) {
            System.out.println("  - " + name);
        }
        System.out.println();

        try {
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException ignored) {}

        Map<String, List<String>> tagMap = new LinkedHashMap<>();
        for (String tool : TOOL_TYPES) tagMap.put(tool + "s", new ArrayList<>());
        tagMap.put("head_armor", new ArrayList<>());
        tagMap.put("chest_armor", new ArrayList<>());
        tagMap.put("leg_armor", new ArrayList<>());
        tagMap.put("foot_armor", new ArrayList<>());

        for (String name : toGenerate) {
            // 收集工具标签（假设所有材质都有工具）
            for (String tool : TOOL_TYPES) {
                tagMap.get(tool + "s").add("mut:" + name + "_" + tool);
            }
            // 收集盔甲标签（假设所有材质都有盔甲）
            tagMap.get("head_armor").add("mut:" + name + "_helmet");
            tagMap.get("chest_armor").add("mut:" + name + "_chestplate");
            tagMap.get("leg_armor").add("mut:" + name + "_leggings");
            tagMap.get("foot_armor").add("mut:" + name + "_boots");
        }

        int count = 0;
        for (Map.Entry<String, List<String>> entry : tagMap.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            StringBuilder json = new StringBuilder("{\n  \"replace\": false,\n  \"values\": [\n");
            List<String> items = entry.getValue();
            for (int i = 0; i < items.size(); i++) {
                json.append("    \"").append(items.get(i)).append("\"");
                if (i < items.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ]\n}\n");
            if (write(TAGS_DIR + "/" + entry.getKey() + ".json", json.toString())) count++;
        }

        System.out.println("标签文件生成完成！共更新 " + count + " 个标签文件\n");
    }

    private static String buildToolModel(String name, String tool) {
        return "{\n  \"parent\": \"item/handheld\",\n  \"textures\": {\n    \"layer0\": \"mut:item/" + name + "_" + tool + "\"\n  }\n}\n";
    }

    private static String buildTools(String name, String cn, String upperName) {
        return """
        package net.mcreator.mut.item;

        import net.mcreator.mut.init.MutMaterials;
        import net.minecraft.world.item.*;

        public abstract class %sTools extends Item {
            public %sTools(Properties p) { super(p); }

            public static class Sword extends SwordItem {
                public Sword() {
                    super(MutMaterials.%s.asToolTier(MutMaterials.ToolType.SWORD),
                            MutMaterials.%s.createToolProperties(MutMaterials.ToolType.SWORD));
                }
            }
            public static class Shovel extends ShovelItem {
                public Shovel() {
                    super(MutMaterials.%s.asToolTier(MutMaterials.ToolType.SHOVEL),
                            MutMaterials.%s.createToolProperties(MutMaterials.ToolType.SHOVEL));
                }
            }
            public static class Pickaxe extends PickaxeItem {
                public Pickaxe() {
                    super(MutMaterials.%s.asToolTier(MutMaterials.ToolType.PICKAXE),
                            MutMaterials.%s.createToolProperties(MutMaterials.ToolType.PICKAXE));
                }
            }
            public static class Axe extends AxeItem {
                public Axe() {
                    super(MutMaterials.%s.asToolTier(MutMaterials.ToolType.AXE),
                            MutMaterials.%s.createToolProperties(MutMaterials.ToolType.AXE));
                }
            }
            public static class Hoe extends HoeItem {
                public Hoe() {
                    super(MutMaterials.%s.asToolTier(MutMaterials.ToolType.HOE),
                            MutMaterials.%s.createToolProperties(MutMaterials.ToolType.HOE));
                }
            }
        }
        """.formatted(
                cn, cn,
                upperName, upperName,
                upperName, upperName,
                upperName, upperName,
                upperName, upperName,
                upperName, upperName
        );
    }

    private static String buildArmor(String name, String cn, String upperName) {
        return """
        package net.mcreator.mut.item;

        import net.mcreator.mut.init.MutMaterials;
        import net.minecraft.core.Holder;
        import net.minecraft.core.registries.BuiltInRegistries;
        import net.minecraft.resources.ResourceLocation;
        import net.minecraft.world.item.*;

        public abstract class %sArmor extends ArmorItem {
            
            private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER = 
                net.minecraft.core.Registry.registerForHolder(
                    BuiltInRegistries.ARMOR_MATERIAL,
                    ResourceLocation.fromNamespaceAndPath("mut", "%s"),
                    MutMaterials.%s.asArmorMaterial()
                );
            
            public %sArmor(Type type, Properties properties) { 
                super(ARMOR_MATERIAL_HOLDER, type, properties); 
            }

            public static class Helmet extends %sArmor {
                public Helmet() { super(Type.HELMET, MutMaterials.%s.createArmorProperties(Type.HELMET)); }
            }
            public static class Chestplate extends %sArmor {
                public Chestplate() { super(Type.CHESTPLATE, MutMaterials.%s.createArmorProperties(Type.CHESTPLATE)); }
            }
            public static class Leggings extends %sArmor {
                public Leggings() { super(Type.LEGGINGS, MutMaterials.%s.createArmorProperties(Type.LEGGINGS)); }
            }
            public static class Boots extends %sArmor {
                public Boots() { super(Type.BOOTS, MutMaterials.%s.createArmorProperties(Type.BOOTS)); }
            }
        }
        """.formatted(
                cn, name, upperName, cn,
                cn, upperName,
                cn, upperName,
                cn, upperName,
                cn, upperName
        );
    }

    private static void printRegistrationCode(List<MutMaterials.MutMaterial> materials) {
        System.out.println("========== 复制以下代码到 MutModItems ==========\n");
        for (MutMaterials.MutMaterial mat : materials) {
            String name = mat.name;
            String upperName = name.toUpperCase();
            String cn = capitalize(name);

            if (mat.hasTools) {
                System.out.println("// " + name + " tools");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_SWORD    = REGISTRY.register(\"" + name + "_sword\",    " + cn + "Tools.Sword::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_SHOVEL   = REGISTRY.register(\"" + name + "_shovel\",   " + cn + "Tools.Shovel::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_PICKAXE  = REGISTRY.register(\"" + name + "_pickaxe\",  " + cn + "Tools.Pickaxe::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_AXE      = REGISTRY.register(\"" + name + "_axe\",      " + cn + "Tools.Axe::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_HOE      = REGISTRY.register(\"" + name + "_hoe\",      " + cn + "Tools.Hoe::new);");
            }

            if (mat.hasArmor) {
                System.out.println("// " + name + " armors");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_HELMET     = REGISTRY.register(\"" + name + "_helmet\",     " + cn + "Armor.Helmet::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_CHESTPLATE = REGISTRY.register(\"" + name + "_chestplate\", " + cn + "Armor.Chestplate::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_LEGGINGS   = REGISTRY.register(\"" + name + "_leggings\",   " + cn + "Armor.Leggings::new);");
                System.out.println("public static final DeferredItem<Item> " + upperName + "_BOOTS      = REGISTRY.register(\"" + name + "_boots\",      " + cn + "Armor.Boots::new);");
            }
            System.out.println();
        }
        System.out.println("========== 代码结束 ==========");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        var sb = new StringBuilder();
        boolean up = true;
        for (char c : s.toCharArray()) {
            if (c == '_') { up = true; continue; }
            sb.append(up ? Character.toUpperCase(c) : c);
            up = false;
        }
        return sb.toString();
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