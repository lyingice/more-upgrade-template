package net.mcreator.mut.init;

import com.google.common.math.Stats;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AnimalArmorMaterials {
    // 添加映射表
    private static final Map<Item, AnimalArmorExtendedStats> STATS_MAP = new HashMap<>();
    private static final AnimalArmorExtendedStats DEFAULT = null;

    // 注册方法
    public static void register(Item armor, AnimalArmorExtendedStats stats) {
        STATS_MAP.put(armor, stats);
    }

    public static AnimalArmorExtendedStats get(Item armor) {
        return STATS_MAP.get(armor);
    }

    // 便捷方法
    public static int durability(Item armor) {
        AnimalArmorExtendedStats stats = get(armor);
        return stats != null ? stats.durability() : 0;
    }

    public static boolean fireResistant(Item armor) {
        AnimalArmorExtendedStats stats = get(armor);
        return stats != null && stats.fireResistant();
    }


    /**
     * 基础记录：只存储材料和基本属性（给旧物品使用）
     */
    public record AnimalArmorBaseStats(
            Holder<ArmorMaterial> material,
            double maxHealthBonus,
            double movementSpeedBonus
    ) {}

    /**
     * 扩展记录：包含耐久和抗火属性（给新物品使用）
     */
    public record AnimalArmorExtendedStats(
            Holder<ArmorMaterial> material,
            double maxHealthBonus,
            double movementSpeedBonus,
            int durability,
            boolean fireResistant,
            Rarity rarity
    ) {
        // 转换方法：从基础记录创建扩展记录
        public AnimalArmorExtendedStats fromBase(AnimalArmorBaseStats base, int durability, boolean fireResistant) {
            return new AnimalArmorExtendedStats(base.material, base.maxHealthBonus, base.movementSpeedBonus, durability, fireResistant,rarity);
        }
    }

    // ========== 旧材料（保持原参数，给已硬编码的物品使用） ==========

    public static final AnimalArmorBaseStats COPPER = registerAnimalBase(
            "copper_animal", 5, 9,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.of(Items.COPPER_INGOT),
            0.0D, 0.0D
    );

    public static final AnimalArmorBaseStats IRON_ANIMAL = registerAnimalBase(
            "iron_animal", 5, 9,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.of(Items.IRON_INGOT),
            0.0D, 0.0D
    );

    public static final AnimalArmorBaseStats GOLDEN_ANIMAL = registerAnimalBase(
            "golden_animal", 7, 25,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F,
            () -> Ingredient.of(Items.GOLD_INGOT),
            0.0D, 0.15D
    );

    public static final AnimalArmorBaseStats DIAMOND_ANIMAL = registerAnimalBase(
            "diamond_animal", 11, 10,
            SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F,
            () -> Ingredient.of(Items.DIAMOND),
            0.0D, 0.0D
    );

    public static final AnimalArmorBaseStats NETHERITE_ANIMAL = registerAnimalBase(
            "netherite_animal", 19, 15,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT),
            0.0D, 0.0D
    );

    // ========== 新材料（使用扩展参数，给新物品使用） ==========
    // ========== 狼铠材料预设（AnimalArmorExtendedStats） ==========

    // 钢
    public static final AnimalArmorExtendedStats STEEL = registerAnimalExtended(
            "steel_animal", 19, 12,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.5F, 0.1F,
            () -> Ingredient.of(MutModItems.STEEL_INGOT.get()),
            4.0D, 0.1D,
            385, true,Rarity.COMMON
    );

    // 精钢
    public static final AnimalArmorExtendedStats ADVANCED_STEEL = registerAnimalExtended(
            "advanced_steel_animal", 23, 15,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.2F,
            () -> Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT.get()),
            8.0D, 0.15D,
            605, true,Rarity.COMMON
    );

    // 鎏金
    public static final AnimalArmorExtendedStats GILDING = registerAnimalExtended(
            "gilding_animal", 19, 22,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.1F,
            () -> Ingredient.of(MutModItems.GILDING_INGOT.get()),
            6.0D, 0.4D,
            363, true,Rarity.COMMON
    );

    // 蓝钻合金
    public static final AnimalArmorExtendedStats BLUE_DIAMOND = registerAnimalExtended(
            "blue_diamond_animal", 23, 18,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5F, 0.1F,
            () -> Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT.get()),
            8.0D, 0.2D,
            495, true,Rarity.COMMON
    );

    // 黑曜石
    public static final AnimalArmorExtendedStats OBSIDIAN = registerAnimalExtended(
            "obsidian_animal", 19, 1,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.0F, 0.15F,
            () -> Ingredient.of(Items.OBSIDIAN),
            10.0D, -0.1D,
            440, true,Rarity.COMMON
    );

    // 下界合金黑曜石
    public static final AnimalArmorExtendedStats NETHERITE_OBSIDIAN = registerAnimalExtended(
            "netherite_obsidian_animal", 23, 1,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.3F,
            () -> Ingredient.of(MutModItems.OBSIDIAN_INGOT.get()),
            20.0D, -0.2D,
            660, true,Rarity.COMMON
    );

    // 悲悯
    public static final AnimalArmorExtendedStats CRYING_OBSIDIAN = registerAnimalExtended(
            "crying_obsidian_animal", 30, 1,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 6.0F, 0.6F,
            () -> Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT.get()),
            30.0D, -0.3D,
            1100, true,Rarity.EPIC
    );

    // 下界之星
    public static final AnimalArmorExtendedStats NETHER_STAR = registerAnimalExtended(
            "nether_star_animal", 19, 22,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 10.0F, 0.1F,
            () -> Ingredient.of(Items.NETHER_STAR),
            10.0D, 0.0D,
            968, true,Rarity.EPIC
    );

    // 龙
    public static final AnimalArmorExtendedStats DRAGON = registerAnimalExtended(
            "dragon_animal", 30, 30,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.1F,
            () -> Ingredient.of(Items.NETHER_STAR),
            20.0D, 0.0D,
            748, true,Rarity.EPIC
    );

    // 凋零
    public static final AnimalArmorExtendedStats WITHER = registerAnimalExtended(
            "wither_animal", 23, 22,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 6.0F, 0.15F,
            () -> Ingredient.of(Items.NETHER_STAR),
            15.0D, 0.2D,
            968, true,Rarity.EPIC
    );

    // 下界合金铜
    public static final AnimalArmorExtendedStats NETHERITE_COPPER = registerAnimalExtended(
            "netherite_copper_animal", 19, 15,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.5F, 0.1F,
            () -> Ingredient.of(Items.COPPER_BLOCK),
            4.0D, 0.0D,
            374, true,Rarity.COMMON
    );

    // 下界合金红石
    public static final AnimalArmorExtendedStats NETHERITE_REDSTONE = registerAnimalExtended(
            "netherite_redstone_animal", 19, 20,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(MutModItems.NETHERITE_REDSTONE_INGOT.get()),
            4.0D, 0.5D,
            429, true,Rarity.COMMON
    );

    // 绿宝石
    public static final AnimalArmorExtendedStats EMERALD = registerAnimalExtended(
            "emerald_animal", 11, 18,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.0F,
            () -> Ingredient.of(Items.EMERALD),
            0.0D, 0.1D,
            330, false,Rarity.COMMON
    );

    // 下界合金绿宝石
    public static final AnimalArmorExtendedStats NETHERITE_EMERALD_ANIMAL = registerAnimalExtended(
            "netherite_emerald_animal_animal", 21, 18,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.1F,
            () -> Ingredient.of(MutModItems.NETHERITE_EMERALD_INGOT.get()),
            0.0D, 0.2D,
            440, true,Rarity.COMMON
    );

    // 紫水晶
    public static final AnimalArmorExtendedStats AMETHYST = registerAnimalExtended(
            "amethyst_animal", 6, 16,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 1.0F, 0.0F,
            () -> Ingredient.of(Items.AMETHYST_SHARD),
            8.0D, 0.0D,
            231, false,Rarity.COMMON
    );

    // 下界合金紫水晶
    public static final AnimalArmorExtendedStats NETHERITE_AMETHYST = registerAnimalExtended(
            "netherite_amethyst_animal", 20, 16,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT.get()),
            16.0D, 0.0D,
            462, true,Rarity.COMMON
    );
    //青金石
    public static final AnimalArmorExtendedStats LAPIS_LAZULI =registerAnimalExtended(
            "lapis_lazuli_animal",5,30,SoundEvents.ARMOR_EQUIP_IRON,0,0F,
            () -> Ingredient.of(Items.LAPIS_LAZULI),0D,0D,97,false,Rarity.COMMON
    );
    //下界合金青金石
    public static final AnimalArmorExtendedStats NETHERITE_LAPIS_LAZULI = registerAnimalExtended(
            "netherite_lapis_lazuli_animal", 19, 60,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.5F, 0.1F,
            () -> Ingredient.of(MutModItems.NETHERITE_LAPIS_LAZULI_INGOT),
            4.0D, 0.0D,
            374, true,Rarity.COMMON
    );

    // ========== 旧材料的工厂方法（8参数） ==========
    private static AnimalArmorBaseStats registerAnimalBase(
            String name,
            int bodyDefense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            double maxHealthBonus,
            double movementSpeedBonus
    ) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BODY, bodyDefense);

        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("mut", name))
        );

        Holder<ArmorMaterial> material = net.minecraft.core.Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath("mut", name),
                new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );

        return new AnimalArmorBaseStats(material, maxHealthBonus, movementSpeedBonus);
    }

    // ========== 新材料的工厂方法（10参数，包含耐久和抗火） ==========
    private static AnimalArmorExtendedStats registerAnimalExtended(
            String name,
            int bodyDefense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            double maxHealthBonus,
            double movementSpeedBonus,
            int durability,
            boolean fireResistant,
            Rarity rarity
    ) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BODY, bodyDefense);

        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("mut", name))
        );

        Holder<ArmorMaterial> material = net.minecraft.core.Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath("mut", name),
                new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );

        return new AnimalArmorExtendedStats(material, maxHealthBonus, movementSpeedBonus, durability, fireResistant,rarity);
    }

    // ========== 属性创建方法（重载，支持两种记录类型） ==========

    // 给旧物品使用
    public static ItemAttributeModifiers createAttributes(AnimalArmorBaseStats stats) {
        var builder = ItemAttributeModifiers.builder();
        String path = stats.material.unwrapKey().orElseThrow().location().getPath();
        double armorValue = stats.material.value().getDefense(ArmorItem.Type.BODY);
        double toughness = stats.material.value().toughness();
        double knockbackRes = stats.material.value().knockbackResistance();

        builder.add(Attributes.ARMOR,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("mut", path + "_armor"),
                        armorValue,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.BODY);

        if (toughness > 0) {
            builder.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_toughness"),
                            toughness,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        if (knockbackRes > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_knockback"),
                            knockbackRes,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        if (stats.maxHealthBonus > 0) {
            builder.add(Attributes.MAX_HEALTH,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_health"),
                            stats.maxHealthBonus,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        if (stats.movementSpeedBonus != 0) {
            builder.add(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_speed"),
                            stats.movementSpeedBonus,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        return builder.build();
    }

    // 给新物品使用
    public static ItemAttributeModifiers createAttributes(AnimalArmorExtendedStats stats) {
        // 复用基础属性的创建逻辑
        AnimalArmorBaseStats baseStats = new AnimalArmorBaseStats(
                stats.material,
                stats.maxHealthBonus,
                stats.movementSpeedBonus
        );
        return createAttributes(baseStats);
    }
}