package net.mcreator.mut.init;

import java.util.*;

public class MutBaseRecipeConfig {

    public record ItemTypeTemplate(
            String itemType,
            String[] pattern,
            boolean hasStick
    ) {}

    // 图案模板
    public static final ItemTypeTemplate SPEAR = new ItemTypeTemplate("spear",
            new String[]{"  a", " b ", "b  "}, true);

    public static final ItemTypeTemplate WOLF_ARMOR = new ItemTypeTemplate("wolf_armor",
            new String[]{"a  ", "aaa", "aba"}, false);

    public static final ItemTypeTemplate HORSE_ARMOR = new ItemTypeTemplate("horse_armor",
            new String[]{"a  ", "aaa", "aba"}, false);

    // 需要生成工作台配方的材料列表（原版材质，下界合金除外）
    public static final List<String> BASE_MATERIALS = Arrays.asList(
            "stone","copper","iron", "golden", "diamond","emerald","amethyst","lapis_lazuli"
    );

    // 特殊材料映射（马铠和狼铠的自定义额外材料）
    private static final Map<String, Map<Character, String>> CUSTOM_KEYS = new HashMap<>();

    static {
        // 狼铠：额外材料 b = 骨头
        CUSTOM_KEYS.put("iron_wolf_armor", Map.of('b', "minecraft:bone"));
        CUSTOM_KEYS.put("golden_wolf_armor", Map.of('b', "minecraft:bone"));
        CUSTOM_KEYS.put("diamond_wolf_armor", Map.of('b', "minecraft:bone"));
        CUSTOM_KEYS.put("emerald_wolf_armor", Map.of('b', "minecraft:bone"));
        CUSTOM_KEYS.put("amethyst_wolf_armor", Map.of('b', "minecraft:bone"));

        // 马铠：额外材料 b = 皮革
        CUSTOM_KEYS.put("iron_horse_armor", Map.of('b', "minecraft:leather"));
        CUSTOM_KEYS.put("golden_horse_armor", Map.of('b', "minecraft:leather"));
        CUSTOM_KEYS.put("diamond_horse_armor", Map.of('b', "minecraft:leather"));
        CUSTOM_KEYS.put("emerald_horse_armor", Map.of('b', "minecraft:leather"));
        CUSTOM_KEYS.put("amethyst_horse_armor", Map.of('b', "minecraft:leather"));
    }

    public static Map<Character, String> getKeys(String material, String itemType) {
        Map<Character, String> keys = new LinkedHashMap<>();
        String materialIngot = getIngotId(material);

        keys.put('a', materialIngot);
        if (getTemplate(itemType).hasStick()) {
            keys.put('b', "minecraft:stick");
        }

        String key = material + "_" + itemType;
        if (CUSTOM_KEYS.containsKey(key)) {
            keys.putAll(CUSTOM_KEYS.get(key));
        }

        return keys;
    }

    private static String getIngotId(String material) {
        return switch (material) {
            case "iron" -> "minecraft:iron_ingot";
            case "golden" -> "minecraft:gold_ingot";
            case "diamond" -> "minecraft:diamond";
            case "emerald" -> "minecraft:emerald";
            case "amethyst" -> "minecraft:amethyst_shard";
            case "stone" -> "minecraft:cobblestone";
            case "copper" -> "minecraft:copper_ingot";
            case "lapis_lazuli" -> "minecraft:lapis_lazuli";
            default -> "mut:" + material + "_ingot";
        };
    }

    public static ItemTypeTemplate getTemplate(String itemType) {
        return switch (itemType) {
            case "spear" -> SPEAR;
            case "wolf_armor" -> WOLF_ARMOR;
            case "horse_armor" -> HORSE_ARMOR;
            default -> null;
        };
    }
}