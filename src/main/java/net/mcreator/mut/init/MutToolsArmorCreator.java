package net.mcreator.mut.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 工具与盔甲类生成器（纯字符串模式，不依赖 Minecraft 运行时）
 */
public class MutToolsArmorCreator {

    private static final String ITEM_DIR = "src/main/java/net/mcreator/mut/item";
    private static final String MODELS_DIR = "src/main/resources/assets/mut/models/item";
    private static final String TAGS_DIR = "src/main/resources/data/mut/tags/item";

    private record MatMeta(
            String name, String constName,
            int miningLevel, float toolSpeed,
            float swordDamage, float shovelDamage, float pickaxeDamage, float axeDamage, float hoeDamage,
            float swordSpeed, float shovelSpeed, float pickaxeSpeed, float axeSpeed, float hoeSpeed,
            int enchantmentValue, int durability, String repairItemExpr,
            boolean fireResistant, boolean hasArmor, int armorDurabilityMultiplier,
            int helmet, int chestplate, int leggings, int boots, int body,
            int armorEnchantmentValue, float armorToughness, float armorKnockbackResistance,
            String equipSound, String textureFolder
    ) {}

    private static final MatMeta[] MATERIALS = {
            new MatMeta("amethyst", "AMETHYST", 2, 7.0F,
                    5.5F, 4.0F, 3.5F, 8F, 1.75F,
                    -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
                    16, 350, "Items.AMETHYST_SHARD",
                    false, true, 21,
                    2, 6, 5, 2, 5,
                    16, 0.5F, 0.0F,
                    "ARMOR_EQUIP_IRON", "amethyst"),
            new MatMeta("netherite_amethyst", "NETHERITE_AMETHYST", 4, 10.0F,
                    7.5F, 6.0F, 5.5F, 9.5F, 2.25F,
                    -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
                    16, 1400, "MutModItems.NETHERITE_AMETHYST_INGOT.get()",
                    true, true, 42,
                    3, 8, 6, 3, 0,
                    14, 3.5F, 0.1F,
                    "ARMOR_EQUIP_NETHERITE", "netherite_amethyst"),
    };


    private static final String[] TOOL_TYPES = {"sword", "shovel", "pickaxe", "axe", "hoe"};
    private static final String[] ARMOR_TYPES = {"helmet", "chestplate", "leggings", "boots"};

    // =====================================================================
    // 入口
    // =====================================================================

    public static void main(String[] args) {
        System.out.println("=== MutToolsArmorCreator 开始生成 ===");
        try {
            Files.createDirectories(Paths.get(ITEM_DIR));
            Files.createDirectories(Paths.get(MODELS_DIR));
            Files.createDirectories(Paths.get(TAGS_DIR));
        } catch (IOException ignored) {}

        int count = 0;
        for (MatMeta m : MATERIALS) {
            String cn = capitalize(m.name());
            if (write(ITEM_DIR + "/" + cn + "Tools.java", buildTools(m, cn))) count++;
            if (m.hasArmor() && write(ITEM_DIR + "/" + cn + "Armor.java", buildArmor(m, cn))) count++;
            count += generateToolModels(m);
        }

        int tagCount = generateTags();

        System.out.println("\n完成！共生成 " + count + " 个文件，" + tagCount + " 个标签文件\n");

        System.out.println("========== 复制以下代码到 MutModItems 的 custom items 块 ==========\n");
        for (MatMeta m : MATERIALS) {
            String cn = capitalize(m.name());
            System.out.println("public static final DeferredItem<Item> " + m.constName() + "_SWORD       = REGISTRY.register(\"" + m.name() + "_sword\",       " + cn + "Tools.Sword::new);");
            System.out.println("public static final DeferredItem<Item> " + m.constName() + "_SHOVEL      = REGISTRY.register(\"" + m.name() + "_shovel\",      " + cn + "Tools.Shovel::new);");
            System.out.println("public static final DeferredItem<Item> " + m.constName() + "_PICKAXE     = REGISTRY.register(\"" + m.name() + "_pickaxe\",     " + cn + "Tools.Pickaxe::new);");
            System.out.println("public static final DeferredItem<Item> " + m.constName() + "_AXE         = REGISTRY.register(\"" + m.name() + "_axe\",         " + cn + "Tools.Axe::new);");
            System.out.println("public static final DeferredItem<Item> " + m.constName() + "_HOE         = REGISTRY.register(\"" + m.name() + "_hoe\",         " + cn + "Tools.Hoe::new);");
            if (m.hasArmor()) {
                System.out.println("public static final DeferredItem<Item> " + m.constName() + "_HELMET      = REGISTRY.register(\"" + m.name() + "_helmet\",      " + cn + "Armor.Helmet::new);");
                System.out.println("public static final DeferredItem<Item> " + m.constName() + "_CHESTPLATE  = REGISTRY.register(\"" + m.name() + "_chestplate\",  " + cn + "Armor.Chestplate::new);");
                System.out.println("public static final DeferredItem<Item> " + m.constName() + "_LEGGINGS    = REGISTRY.register(\"" + m.name() + "_leggings\",    " + cn + "Armor.Leggings::new);");
                System.out.println("public static final DeferredItem<Item> " + m.constName() + "_BOOTS       = REGISTRY.register(\"" + m.name() + "_boots\",       " + cn + "Armor.Boots::new);");
            }
        }
        System.out.println("\n========== 代码结束 ==========");
    }

    // =====================================================================
    // 标签生成
    // =====================================================================

    private static int generateTags() {
        // 收集每个标签对应的物品 ID 列表
        Map<String, List<String>> tagMap = new LinkedHashMap<>();
        for (String tool : TOOL_TYPES) tagMap.put(tool + "s", new ArrayList<>());
        for (String armor : ARMOR_TYPES) tagMap.put(armor.equals("helmet") ? "head_armor" :
                armor.equals("chestplate") ? "chest_armor" :
                        armor.equals("leggings") ? "leg_armor" : "foot_armor", new ArrayList<>());

        for (MatMeta m : MATERIALS) {
            for (String tool : TOOL_TYPES) {
                tagMap.get(tool + "s").add("mut:" + m.name() + "_" + tool);
            }
            if (m.hasArmor()) {
                for (String armor : ARMOR_TYPES) {
                    String tag = armor.equals("helmet") ? "head_armor" :
                            armor.equals("chestplate") ? "chest_armor" :
                                    armor.equals("leggings") ? "leg_armor" : "foot_armor";
                    tagMap.get(tag).add("mut:" + m.name() + "_" + armor);
                }
            }
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
        return count;
    }

    // =====================================================================
    // 工具集模板
    // =====================================================================

    private static String buildTools(MatMeta m, String cn) {
        String fr = m.fireResistant() ? ".fireResistant()" : "";
        String incorrectTag = switch (m.miningLevel()) {
            case 0 -> "INCORRECT_FOR_WOODEN_TOOL";
            case 1 -> "INCORRECT_FOR_STONE_TOOL";
            case 2 -> "INCORRECT_FOR_IRON_TOOL";
            case 3 -> "INCORRECT_FOR_DIAMOND_TOOL";
            case 4 -> "INCORRECT_FOR_NETHERITE_TOOL";
            default -> "INCORRECT_FOR_IRON_TOOL";
        };

        return """
            package net.mcreator.mut.item;

            import net.mcreator.mut.init.MutModItems;
            import net.minecraft.world.level.block.Block;
            import net.minecraft.world.item.crafting.Ingredient;
            import net.minecraft.world.item.*;
            import net.minecraft.tags.TagKey;
            import net.minecraft.tags.BlockTags;
            import net.mcreator.mut.init.MutMaterials;
            import net.mcreator.mut.init.MutMaterials.ToolType;

            public abstract class %sTools extends Item {
                public %sTools(Properties p) { super(p); }

                // ── 剑 ──
                public static class Sword extends SwordItem {
                    private static final Tier TIER = new Tier() {
                        @Override public int getUses() { return %d; }
                        @Override public float getSpeed() { return %.1ff; }
                        @Override public float getAttackDamageBonus() { return %.1ff; }
                        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.%s; }
                        @Override public int getEnchantmentValue() { return %d; }
                        @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(%s)); }
                    };
                    public Sword() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, ToolType.SWORD))); }
                }

                // ── 锹 ──
                public static class Shovel extends ShovelItem {
                    private static final Tier TIER = new Tier() {
                        @Override public int getUses() { return %d; }
                        @Override public float getSpeed() { return %.1ff; }
                        @Override public float getAttackDamageBonus() { return %.1ff; }
                        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.%s; }
                        @Override public int getEnchantmentValue() { return %d; }
                        @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(%s)); }
                    };
                    public Shovel() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, ToolType.SHOVEL))); }
                }

                // ── 镐 ──
                public static class Pickaxe extends PickaxeItem {
                    private static final Tier TIER = new Tier() {
                        @Override public int getUses() { return %d; }
                        @Override public float getSpeed() { return %.1ff; }
                        @Override public float getAttackDamageBonus() { return %.1ff; }
                        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.%s; }
                        @Override public int getEnchantmentValue() { return %d; }
                        @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(%s)); }
                    };
                    public Pickaxe() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, ToolType.PICKAXE))); }
                }

                // ── 斧 ──
                public static class Axe extends AxeItem {
                    private static final Tier TIER = new Tier() {
                        @Override public int getUses() { return %d; }
                        @Override public float getSpeed() { return %.1ff; }
                        @Override public float getAttackDamageBonus() { return %.1ff; }
                        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.%s; }
                        @Override public int getEnchantmentValue() { return %d; }
                        @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(%s)); }
                    };
                    public Axe() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, ToolType.AXE))); }
                }

                // ── 锄 ──
                public static class Hoe extends HoeItem {
                    private static final Tier TIER = new Tier() {
                        @Override public int getUses() { return %d; }
                        @Override public float getSpeed() { return %.1ff; }
                        @Override public float getAttackDamageBonus() { return %.1ff; }
                        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.%s; }
                        @Override public int getEnchantmentValue() { return %d; }
                        @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(%s)); }
                    };
                    public Hoe() { super(TIER, new Item.Properties()%s.attributes(MutMaterials.createToolAttributes(MutMaterials.%s, ToolType.HOE))); }
                }
            }
            """.formatted(
                cn, cn,
                m.durability(), m.toolSpeed(), m.swordDamage(), incorrectTag, m.enchantmentValue(), m.repairItemExpr(),
                fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.constName(),
                m.durability(), m.toolSpeed(), m.shovelDamage(), incorrectTag, m.enchantmentValue(), m.repairItemExpr(),
                fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.constName(),
                m.durability(), m.toolSpeed(), m.pickaxeDamage(), incorrectTag, m.enchantmentValue(), m.repairItemExpr(),
                fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.constName(),
                m.durability(), m.toolSpeed(), m.axeDamage(), incorrectTag, m.enchantmentValue(), m.repairItemExpr(),
                fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.constName(),
                m.durability(), m.toolSpeed(), m.hoeDamage(), incorrectTag, m.enchantmentValue(), m.repairItemExpr(),
                fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.constName()
        );
    }

    // =====================================================================
    // 工具模型 JSON
    // =====================================================================

    private static int generateToolModels(MatMeta m) {
        int count = 0;
        for (String tool : TOOL_TYPES) {
            if (write(MODELS_DIR + "/" + m.name() + "_" + tool + ".json",
                    "{\n  \"parent\": \"item/handheld\",\n  \"textures\": {\n    \"layer0\": \"mut:item/" + m.name() + "_" + tool + "\"\n  }\n}\n")) count++;
        }
        return count;
    }

    // =====================================================================
    // 盔甲集模板
    // =====================================================================

    private static String buildArmor(MatMeta m, String cn) {
        String fr = m.fireResistant() ? ".fireResistant()" : "";
        return """
            package net.mcreator.mut.item;

            import net.minecraft.core.Holder;
            import net.minecraft.core.registries.BuiltInRegistries;
            import net.minecraft.resources.ResourceLocation;
            import net.minecraft.sounds.SoundEvents;
            import net.minecraft.Util;
            import net.minecraft.world.item.*;
            import net.minecraft.world.item.crafting.Ingredient;
            import net.mcreator.mut.init.MutMaterials;

            import java.util.EnumMap;
            import java.util.List;

            public abstract class %sArmor extends ArmorItem {

                public static final Holder<ArmorMaterial> ARMOR_MATERIAL =
                        net.minecraft.core.Registry.registerForHolder(
                                BuiltInRegistries.ARMOR_MATERIAL,
                                ResourceLocation.fromNamespaceAndPath("mut", "%s"),
                                new ArmorMaterial(
                                        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                                            map.put(ArmorItem.Type.BOOTS, %d);
                                            map.put(ArmorItem.Type.LEGGINGS, %d);
                                            map.put(ArmorItem.Type.CHESTPLATE, %d);
                                            map.put(ArmorItem.Type.HELMET, %d);
                                            map.put(ArmorItem.Type.BODY, %d);
                                        }),
                                        %d,
                                        SoundEvents.%s,
                                        () -> Ingredient.of(),
                                        List.of(new ArmorMaterial.Layer(
                                                ResourceLocation.fromNamespaceAndPath("mut", "%s")
                                        )),
                                        %.1ff,
                                        %.2ff
                                )
                        );

                public %sArmor(ArmorItem.Type type, Item.Properties properties) { super(ARMOR_MATERIAL, type, properties); }

                public static class Helmet extends %sArmor {
                    public Helmet() { super(ArmorItem.Type.HELMET, new Item.Properties()%s.durability(ArmorItem.Type.HELMET.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, ArmorItem.Type.HELMET))); }
                }
                public static class Chestplate extends %sArmor {
                    public Chestplate() { super(ArmorItem.Type.CHESTPLATE, new Item.Properties()%s.durability(ArmorItem.Type.CHESTPLATE.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, ArmorItem.Type.CHESTPLATE))); }
                }
                public static class Leggings extends %sArmor {
                    public Leggings() { super(ArmorItem.Type.LEGGINGS, new Item.Properties()%s.durability(ArmorItem.Type.LEGGINGS.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, ArmorItem.Type.LEGGINGS))); }
                }
                public static class Boots extends %sArmor {
                    public Boots() { super(ArmorItem.Type.BOOTS, new Item.Properties()%s.durability(ArmorItem.Type.BOOTS.getDurability(%d)).attributes(MutMaterials.createArmorAttributes(MutMaterials.%s, ArmorItem.Type.BOOTS))); }
                }
            }
            """.formatted(
                cn, m.name(),
                m.boots(), m.leggings(), m.chestplate(), m.helmet(), m.body(),
                m.armorEnchantmentValue(), m.equipSound(), m.textureFolder(),
                m.armorToughness(), m.armorKnockbackResistance(),
                cn,
                cn, fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.armorDurabilityMultiplier(), m.constName(),
                cn, fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.armorDurabilityMultiplier(), m.constName(),
                cn, fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.armorDurabilityMultiplier(), m.constName(),
                cn, fr.isEmpty() ? "" : "\n                                    .fireResistant()", m.armorDurabilityMultiplier(), m.constName()
        );
    }

    // =====================================================================
    // 辅助
    // =====================================================================

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