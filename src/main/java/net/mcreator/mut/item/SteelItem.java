package net.mcreator.mut.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.mcreator.mut.util.ArmorMaterialConfig;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.Util;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.EnumMap;
import java.util.Optional;

@EventBusSubscriber
public abstract class SteelItem extends ArmorItem {
    public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

    @SubscribeEvent
    public static void registerArmorMaterial(RegisterEvent event) {
        event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
            System.out.println("[SteelItem] Registering armor material...");

            ArmorMaterialConfig config = null;

            // 直接读取 JSON 文件
            try {
                var resource = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("data/mut/armor_materials/steel.json");
                if (resource != null) {
                    try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonObject obj = gson.fromJson(reader, JsonObject.class);
                        config = ArmorMaterialConfig.fromJson("steel", obj);
                        System.out.println("[SteelItem] Loaded config from file");
                        System.out.println("[SteelItem] bootsDefense from file: " + config.bootsDefense);
                    }
                } else {
                    System.out.println("[SteelItem] Config file not found, using hardcoded values");
                }
            } catch (Exception e) {
                System.err.println("[SteelItem] Failed to read config file: " + e);
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
                System.out.println("[SteelItem] Loaded config from file");
            } else {
                // 回退到硬编码
                armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 3);
                    map.put(ArmorItem.Type.LEGGINGS, 6);
                    map.put(ArmorItem.Type.CHESTPLATE, 8);
                    map.put(ArmorItem.Type.HELMET, 3);
                    map.put(ArmorItem.Type.BODY, 8);
                }), 9, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")),
                        () -> Ingredient.of(),
                        List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:steel"))),
                        2.5f, 0.1f);
                System.out.println("[SteelItem] Using hardcoded values");
            }

            registerHelper.register(ResourceLocation.parse("mut:steel"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
        });
    }

    // ========== 以下代码保持不变 ==========
    public SteelItem(ArmorItem.Type type, Item.Properties properties) {
        super(ARMOR_MATERIAL, type, properties);
    }

    public static class Helmet extends SteelItem {
        public Helmet() {
            super(ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(35)).fireResistant());
        }
    }

    public static class Chestplate extends SteelItem {
        public Chestplate() {
            super(ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(35)).fireResistant());
        }
    }

    public static class Leggings extends SteelItem {
        public Leggings() {
            super(ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(35)).fireResistant());
        }
    }

    public static class Boots extends SteelItem {
        public Boots() {
            super(ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(35)).fireResistant());
        }
    }
}