package net.mcreator.mut.init;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 工具与盔甲类生成器（材料列表驱动）
 * 用法：
 *   1. 修改 MATERIALS_TO_GENERATE 数组，填入要生成的材质名
 *   2. 运行 main 方法
 *   3. 如果要生成所有材质，将 GENERATE_ALL 改为 true
 */
public class MutToolsArmorCreator {

    private static final String ITEM_DIR = "src/main/java/net/mcreator/mut/item";
    private static final String MODELS_DIR = "src/main/resources/assets/mut/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/mut/tags/item";

    private static final String[] TOOL_TYPES = {"sword", "shovel", "pickaxe", "axe", "hoe"};

    // ========== 在这里选择要生成的材质 ==========
    // 方式1：指定要生成的材质名（只生成这些）
    private static final String[] MATERIALS_TO_GENERATE = {
            "netherite_amethyst"
    };

    // 方式2：设为 true 则生成所有材质（忽略上面的列表）
    private static final boolean GENERATE_ALL = false;
    // ===========================================

    public static void main(String[] args) {
        System.out.println("=== MutToolsArmorCreator 开始生成 ===\n");
        System.out.println("数据来源: MutMaterials");

        // 获取要生成的材质列表
        List<MutMaterials.MutMaterial> materialsToGenerate = getMaterialsToGenerate();

        if (materialsToGenerate.isEmpty()) {
            System.out.println("没有找到要生成的材质！");
            System.out.println("\n可用的材质列表:");
            for (MutMaterials.MutMaterial mat : MutMaterials.getAll()) {
                System.out.println("  - " + mat.name + " (工具:" + mat.hasTools + ", 盔甲:" + mat.hasArmor + ")");
            }
            return;
        }

        System.out.println("将要生成以下材质:");
        for (MutMaterials.MutMaterial mat : materialsToGenerate) {
            System.out.println("  - " + mat.name + " (工具:" + mat.hasTools + ", 盔甲:" + mat.hasArmor + ")");
        }
        System.out.println();

        try {
            Files.createDirectories(Paths.get(ITEM_DIR));
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException ignored) {}

        int count = 0;

        // 收集所有生成的物品 ID，用于标签生成
        Map<String, List<String>> tagMap = new LinkedHashMap<>();
        for (String tool : TOOL_TYPES) tagMap.put(tool + "s", new ArrayList<>());
        tagMap.put("head_armor", new ArrayList<>());
        tagMap.put("chest_armor", new ArrayList<>());
        tagMap.put("leg_armor", new ArrayList<>());
        tagMap.put("foot_armor", new ArrayList<>());

        for (MutMaterials.MutMaterial mat : materialsToGenerate) {
            String name = mat.name;
            String cn = capitalize(name);

            // 根据 hasTools 决定是否生成工具类
            if (mat.hasTools) {
                if (write(ITEM_DIR + "/" + cn + "Tools.java", buildTools(mat, cn))) count++;
                // 生成工具模型
                for (String tool : TOOL_TYPES) {
                    String modelPath = MODELS_DIR + "/" + name + "_" + tool + ".json";
                    if (write(modelPath, buildToolModel(name, tool))) count++;
                }
                // 收集工具标签
                for (String tool : TOOL_TYPES) {
                    tagMap.get(tool + "s").add("mut:" + name + "_" + tool);
                }
            }

            // 根据 hasArmor 决定是否生成盔甲类
            if (mat.hasArmor) {
                if (write(ITEM_DIR + "/" + cn + "Armor.java", buildArmor(mat, cn))) count++;
                // 收集盔甲标签
                tagMap.get("head_armor").add("mut:" + name + "_helmet");
                tagMap.get("chest_armor").add("mut:" + name + "_chestplate");
                tagMap.get("leg_armor").add("mut:" + name + "_leggings");
                tagMap.get("foot_armor").add("mut:" + name + "_boots");
            }
        }

        // 生成标签文件
        int tagCount = generateTagFiles(tagMap);

        System.out.println("\n完成！共生成 " + count + " 个文件，更新了 " + tagCount + " 个标签文件\n");
        printRegistrationCode(materialsToGenerate);
    }

    /**
     * 获取要生成的材质列表
     */
    private static List<MutMaterials.MutMaterial> getMaterialsToGenerate() {
        List<String> targetNames = new ArrayList<>();

        if (GENERATE_ALL) {
            // 生成所有材质
            return new ArrayList<>(MutMaterials.getAll());
        }

        // 只生成指定的材质
        targetNames.addAll(Arrays.asList(MATERIALS_TO_GENERATE));

        if (targetNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<MutMaterials.MutMaterial> result = new ArrayList<>();
        for (String name : targetNames) {
            for (MutMaterials.MutMaterial mat : MutMaterials.getAll()) {
                if (mat.name.equalsIgnoreCase(name)) {
                    result.add(mat);
                    break;
                }
            }
        }
        return result;
    }

    // ========== 构建方法 ==========

    private static String buildToolModel(String name, String tool) {
        return "{\n  \"parent\": \"item/handheld\",\n  \"textures\": {\n    \"layer0\": \"mut:item/" + name + "_" + tool + "\"\n  }\n}\n";
    }

    private static String buildTools(MutMaterials.MutMaterial mat, String cn) {
        String fireResistant = mat.fireResistant ? "\n                                    .fireResistant()" : "";

        return """
            package net.mcreator.mut.item;

            import net.mcreator.mut.init.MutMaterials;
            import net.minecraft.world.item.*;

            public abstract class %sTools extends Item {
                public %sTools(Properties p) { super(p); }

                public static class Sword extends SwordItem {
                    private static final Tier TIER = MutMaterials.%s.asToolTier(MutMaterials.ToolType.SWORD);
                    public Sword() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, MutMaterials.ToolType.SWORD))); }
                }
                public static class Shovel extends ShovelItem {
                    private static final Tier TIER = MutMaterials.%s.asToolTier(MutMaterials.ToolType.SHOVEL);
                    public Shovel() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, MutMaterials.ToolType.SHOVEL))); }
                }
                public static class Pickaxe extends PickaxeItem {
                    private static final Tier TIER = MutMaterials.%s.asToolTier(MutMaterials.ToolType.PICKAXE);
                    public Pickaxe() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, MutMaterials.ToolType.PICKAXE))); }
                }
                public static class Axe extends AxeItem {
                    private static final Tier TIER = MutMaterials.%s.asToolTier(MutMaterials.ToolType.AXE);
                    public Axe() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, MutMaterials.ToolType.AXE))); }
                }
                public static class Hoe extends HoeItem {
                    private static final Tier TIER = MutMaterials.%s.asToolTier(MutMaterials.ToolType.HOE);
                    public Hoe() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, MutMaterials.ToolType.HOE))); }
                }
            }
            """.formatted(
                cn, cn,
                mat.name.toUpperCase(), fireResistant, mat.name.toUpperCase(),
                mat.name.toUpperCase(), fireResistant, mat.name.toUpperCase(),
                mat.name.toUpperCase(), fireResistant, mat.name.toUpperCase(),
                mat.name.toUpperCase(), fireResistant, mat.name.toUpperCase(),
                mat.name.toUpperCase(), fireResistant, mat.name.toUpperCase()
        );
    }

    private static String buildArmor(MutMaterials.MutMaterial mat, String cn) {
        String fireResistant = mat.fireResistant ? "\n                                    .fireResistant()" : "";
        int durability = mat.durability * mat.armorDurabilityFactor;

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
                    public Helmet() { super(Type.HELMET, new Item.Properties()%s.durability(Type.HELMET.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, Type.HELMET))); }
                }
                public static class Chestplate extends %sArmor {
                    public Chestplate() { super(Type.CHESTPLATE, new Item.Properties()%s.durability(Type.CHESTPLATE.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, Type.CHESTPLATE))); }
                }
                public static class Leggings extends %sArmor {
                    public Leggings() { super(Type.LEGGINGS, new Item.Properties()%s.durability(Type.LEGGINGS.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, Type.LEGGINGS))); }
                }
                public static class Boots extends %sArmor {
                    public Boots() { super(Type.BOOTS, new Item.Properties()%s.durability(Type.BOOTS.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, Type.BOOTS))); }
                }
            }
            """.formatted(
                cn, mat.name, mat.name.toUpperCase(), cn,
                cn, fireResistant, durability, mat.name.toUpperCase(),
                cn, fireResistant, durability, mat.name.toUpperCase(),
                cn, fireResistant, durability, mat.name.toUpperCase(),
                cn, fireResistant, durability, mat.name.toUpperCase()
        );
    }

    // ========== 标签文件生成 ==========

    private static int generateTagFiles(Map<String, List<String>> tagMap) {
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
        return count;
    }

    // ========== 注册代码输出 ==========

    private static void printRegistrationCode(List<MutMaterials.MutMaterial> materials) {
        System.out.println("========== 复制以下代码到 MutModItems 的 custom items 块 ==========\n");
        for (MutMaterials.MutMaterial mat : materials) {
            String cn = capitalize(mat.name);

            if (mat.hasTools) {
                System.out.println("// " + mat.name + " 工具");
                System.out.println("public static final DeferredItem<Item> " + cn + "_SWORD    = REGISTRY.register(\"" + mat.name + "_sword\",    " + cn + "Tools.Sword::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_SHOVEL   = REGISTRY.register(\"" + mat.name + "_shovel\",   " + cn + "Tools.Shovel::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_PICKAXE  = REGISTRY.register(\"" + mat.name + "_pickaxe\",  " + cn + "Tools.Pickaxe::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_AXE      = REGISTRY.register(\"" + mat.name + "_axe\",      " + cn + "Tools.Axe::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_HOE      = REGISTRY.register(\"" + mat.name + "_hoe\",      " + cn + "Tools.Hoe::new);");
            }

            if (mat.hasArmor) {
                System.out.println("// " + mat.name + " 盔甲");
                System.out.println("public static final DeferredItem<Item> " + cn + "_HELMET     = REGISTRY.register(\"" + mat.name + "_helmet\",     " + cn + "Armor.Helmet::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_CHESTPLATE = REGISTRY.register(\"" + mat.name + "_chestplate\", " + cn + "Armor.Chestplate::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_LEGGINGS   = REGISTRY.register(\"" + mat.name + "_leggings\",   " + cn + "Armor.Leggings::new);");
                System.out.println("public static final DeferredItem<Item> " + cn + "_BOOTS      = REGISTRY.register(\"" + mat.name + "_boots\",      " + cn + "Armor.Boots::new);");
            }
            System.out.println();
        }
        System.out.println("========== 代码结束 ==========");
    }

    // ========== 辅助方法 ==========

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