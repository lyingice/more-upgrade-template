package net.mcreator.mut.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.mcreator.mut.init.MutMoreAttributeMaterials;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.Util;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.util.ArmorMaterialConfig;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.EnumMap;

@EventBusSubscriber
public abstract class CryingObsidianItem extends ArmorItem {
    public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

    @SubscribeEvent
    public static void registerArmorMaterial(RegisterEvent event) {
        event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
            System.out.println("[CryingObsidianItem] Registering armor material...");

            ArmorMaterialConfig config = null;

            // 直接读取 JSON 文件
            try {
                var resource = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("data/mut/armor_materials/crying_obsidian.json");
                if (resource != null) {
                    try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonObject obj = gson.fromJson(reader, JsonObject.class);
                        config = ArmorMaterialConfig.fromJson("crying_obsidian", obj);
                        System.out.println("[CryingObsidianItem] Loaded config from file");
                        System.out.println("[CryingObsidianItem] bootsDefense from file: " + config.bootsDefense);
                    }
                } else {
                    System.out.println("[CryingObsidianItem] Config file not found, using hardcoded values");
                }
            } catch (Exception e) {
                System.err.println("[CryingObsidianItem] Failed to read config file: " + e);
            }

            ArmorMaterial armorMaterial;

            if (config != null) {
                // 使用文件配置
                EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
                defense.put(ArmorItem.Type.BOOTS, config.bootsDefense);
                defense.put(ArmorItem.Type.LEGGINGS, config.leggingsDefense);
                defense.put(ArmorItem.Type.CHESTPLATE, config.chestplateDefense);
                defense.put(ArmorItem.Type.HELMET, config.helmetDefense);
                defense.put(ArmorItem.Type.BODY, config.bodyDefense);

                armorMaterial = new ArmorMaterial(
                        defense,
                        config.enchantmentValue,
                        config.equipSound,
                        config.repairIngredientSupplier,
                        List.of(new ArmorMaterial.Layer(config.textureLocation)),
                        config.toughness,
                        config.knockbackResistance
                );
                System.out.println("[CryingObsidianItem] Loaded config from file");
            } else {
                // 回退到硬编码
                armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 5);
                    map.put(ArmorItem.Type.LEGGINGS, 10);
                    map.put(ArmorItem.Type.CHESTPLATE, 12);
                    map.put(ArmorItem.Type.HELMET, 5);
                    map.put(ArmorItem.Type.BODY, 12);
                }), 1, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")),
                        () -> Ingredient.of(new ItemStack(MutModItems.CRYING_OBSIDIAN_INGOT.get())),
                        List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:crying_obsidian"))),
                        4f, 0.2f);
                System.out.println("[CryingObsidianItem] Using hardcoded values");
            }

            registerHelper.register(ResourceLocation.parse("mut:crying_obsidian"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
        });
    }

    public CryingObsidianItem(ArmorItem.Type type, Item.Properties properties) {
        super(ARMOR_MATERIAL, type, properties);
    }

    // 构建完整属性的方法（从配置读取）
    private static ItemAttributeModifiers buildAttributes(ArmorItem.Type type, ArmorMaterialConfig config) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        EquipmentSlotGroup slot = switch (type) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };

        // 护甲值（从配置获取）
        builder.add(Attributes.ARMOR,
                new AttributeModifier(ResourceLocation.parse("mut:armor_" + type.getName()),
                        config.getDefense(type), AttributeModifier.Operation.ADD_VALUE),
                slot);

        // 盔甲韧性
        builder.add(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(ResourceLocation.parse("mut:toughness_" + type.getName()),
                        config.toughness, AttributeModifier.Operation.ADD_VALUE),
                slot);

        // 击退抗性
        if (config.knockbackResistance > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ResourceLocation.parse("mut:knockback_" + type.getName()),
                            config.knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    slot);
        }

        // ========== 从 MutMoreAttributeMaterials 获取额外属性 ==========
        List<MutMoreAttributeMaterials.AttributeEntry> extraEntries = switch (type) {
            case HELMET -> MutMoreAttributeMaterials.getHelmetAttributes(config.name);
            case CHESTPLATE -> MutMoreAttributeMaterials.getChestplateAttributes(config.name);
            case LEGGINGS -> MutMoreAttributeMaterials.getLeggingsAttributes(config.name);
            case BOOTS -> MutMoreAttributeMaterials.getBootsAttributes(config.name);
            default -> Collections.emptyList();
        };

        for (MutMoreAttributeMaterials.AttributeEntry entry : extraEntries) {
            builder.add(entry.attribute(),
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", config.name + "_" + entry.suffixId()),
                            entry.amount(),
                            entry.operation()
                    ),
                    entry.slotGroup()
            );
        }

        return builder.build();
    }

    // 默认属性（数据包不存在时使用，保持原有特效）
    private static ItemAttributeModifiers buildDefaultAttributes(ArmorItem.Type type) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        EquipmentSlotGroup slot = switch (type) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };

        double armorValue = switch (type) {
            case HELMET -> 5;
            case CHESTPLATE -> 12;
            case LEGGINGS -> 10;
            case BOOTS -> 5;
            default -> 0;
        };

        builder.add(Attributes.ARMOR,
                new AttributeModifier(ResourceLocation.parse("mut:armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(ResourceLocation.parse("mut:toughness_" + type.getName()), 4.0, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(ResourceLocation.parse("mut:knockback_" + type.getName()), 0.2, AttributeModifier.Operation.ADD_VALUE),
                slot);

        // 生命值 +7.5%
        builder.add(Attributes.MAX_HEALTH,
                new AttributeModifier(ResourceLocation.parse("mut:health_" + type.getName()), 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                slot);

        // 移动速度 -2.5%
        builder.add(Attributes.MOVEMENT_SPEED,
                new AttributeModifier(ResourceLocation.parse("mut:speed_" + type.getName()), -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                slot);

        return builder.build();
    }

    public static class Helmet extends CryingObsidianItem {
        public Helmet() {
            super(ArmorItem.Type.HELMET, createProperties(ArmorItem.Type.HELMET));
        }
    }

    public static class Chestplate extends CryingObsidianItem {
        public Chestplate() {
            super(ArmorItem.Type.CHESTPLATE, createProperties(ArmorItem.Type.CHESTPLATE));
        }
    }

    public static class Leggings extends CryingObsidianItem {
        public Leggings() {
            super(ArmorItem.Type.LEGGINGS, createProperties(ArmorItem.Type.LEGGINGS));
        }
    }

    public static class Boots extends CryingObsidianItem {
        public Boots() {
            super(ArmorItem.Type.BOOTS, createProperties(ArmorItem.Type.BOOTS));
        }
    }

    private static Item.Properties createProperties(ArmorItem.Type type) {
        ArmorMaterialConfig config = null;

        // 直接读取 JSON 文件
        try {
            var resource = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("data/mut/armor_materials/crying_obsidian.json");
            if (resource != null) {
                try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject obj = gson.fromJson(reader, JsonObject.class);
                    config = ArmorMaterialConfig.fromJson("crying_obsidian", obj);
                    System.out.println("[CryingObsidianItem] createProperties loaded config, boots=" + config.bootsDefense);
                }
            }
        } catch (Exception e) {
            System.err.println("[CryingObsidianItem] createProperties failed to read config: " + e);
        }

        if (config != null) {
            return new Item.Properties()
                    .durability(type.getDurability(config.durabilityMultiplier))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildAttributes(type, config));
        } else {
            return new Item.Properties()
                    .durability(type.getDurability(100))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildDefaultAttributes(type));
        }
    }
}