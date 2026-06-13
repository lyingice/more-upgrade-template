package net.mcreator.mut.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.mcreator.mut.init.MutMoreAttributeMaterials;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.Util;

import net.mcreator.mut.util.ArmorMaterialConfig;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.EnumMap;

@EventBusSubscriber
public abstract class NetherStarItem extends ArmorItem {
    public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

    @SubscribeEvent
    public static void registerArmorMaterial(RegisterEvent event) {
        event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
            System.out.println("[NetherStarItem] Registering armor material...");

            ArmorMaterialConfig config = null;

            // 直接读取 JSON 文件
            try {
                var resource = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("data/mut/armor_materials/nether_star.json");
                if (resource != null) {
                    try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonObject obj = gson.fromJson(reader, JsonObject.class);
                        config = ArmorMaterialConfig.fromJson("nether_star", obj);
                        System.out.println("[NetherStarItem] Loaded config from file");
                        System.out.println("[NetherStarItem] bootsDefense from file: " + config.bootsDefense);
                    }
                } else {
                    System.out.println("[NetherStarItem] Config file not found, using hardcoded values");
                }
            } catch (Exception e) {
                System.err.println("[NetherStarItem] Failed to read config file: " + e);
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
                System.out.println("[NetherStarItem] Loaded config from file");
            } else {
                // 回退到硬编码
                armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 3);
                    map.put(ArmorItem.Type.LEGGINGS, 6);
                    map.put(ArmorItem.Type.CHESTPLATE, 8);
                    map.put(ArmorItem.Type.HELMET, 3);
                    map.put(ArmorItem.Type.BODY, 8);
                }), 22, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")),
                        () -> Ingredient.of(new ItemStack(Items.NETHER_STAR)),
                        List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:nether_star"))),
                        10f, 0.1f);
                System.out.println("[NetherStarItem] Using hardcoded values");
            }

            registerHelper.register(ResourceLocation.parse("mut:nether_star"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
        });
    }

    public NetherStarItem(ArmorItem.Type type, Item.Properties properties) {
        super(ARMOR_MATERIAL, type, properties);
    }

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

    private static ItemAttributeModifiers buildDefaultAttributes(ArmorItem.Type type, double healthBonus) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        EquipmentSlotGroup slot = switch (type) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };

        double armorValue = switch (type) {
            case HELMET -> 3;
            case CHESTPLATE -> 8;
            case LEGGINGS -> 6;
            case BOOTS -> 3;
            default -> 0;
        };

        builder.add(Attributes.ARMOR,
                new AttributeModifier(ResourceLocation.parse("mut:nether_star_armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(ResourceLocation.parse("mut:nether_star_toughness_" + type.getName()), 10.0, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(ResourceLocation.parse("mut:nether_star_knockback_" + type.getName()), 0.1, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.MAX_HEALTH,
                new AttributeModifier(ResourceLocation.parse("mut:nether_star_health_" + type.getName()), healthBonus, AttributeModifier.Operation.ADD_VALUE),
                slot);

        return builder.build();
    }

    private static Item.Properties createProperties(ArmorItem.Type type) {
        ArmorMaterialConfig config = null;

        // 直接读取 JSON 文件
        try {
            var resource = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("data/mut/armor_materials/nether_star.json");
            if (resource != null) {
                try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject obj = gson.fromJson(reader, JsonObject.class);
                    config = ArmorMaterialConfig.fromJson("nether_star", obj);
                    System.out.println("[NetherStarItem] createProperties loaded config, boots=" + config.bootsDefense);
                }
            }
        } catch (Exception e) {
            System.err.println("[NetherStarItem] createProperties failed to read config: " + e);
        }

        if (config != null) {
            return new Item.Properties()
                    .durability(type.getDurability(config.durabilityMultiplier))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildAttributes(type, config));
        } else {
            return new Item.Properties()
                    .durability(type.getDurability(88))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildDefaultAttributes(type, 5.0));
        }
    }

    public static class Helmet extends NetherStarItem {
        public Helmet() {
            super(ArmorItem.Type.HELMET, createProperties(ArmorItem.Type.HELMET));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
    }

    public static class Chestplate extends NetherStarItem {
        public Chestplate() {
            super(ArmorItem.Type.CHESTPLATE, createProperties(ArmorItem.Type.CHESTPLATE));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
    }

    public static class Leggings extends NetherStarItem {
        public Leggings() {
            super(ArmorItem.Type.LEGGINGS, createProperties(ArmorItem.Type.LEGGINGS));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
    }

    public static class Boots extends NetherStarItem {
        public Boots() {
            super(ArmorItem.Type.BOOTS, createProperties(ArmorItem.Type.BOOTS));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
    }
}