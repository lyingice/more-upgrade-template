package net.mcreator.mut.item;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

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

import net.mcreator.mut.init.MutModItems;

import java.util.List;
import java.util.EnumMap;

@EventBusSubscriber
public abstract class AdvancedSteelItem extends ArmorItem {
    public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

    @SubscribeEvent
    public static void registerArmorMaterial(RegisterEvent event) {
        System.out.println("[AdvancedSteelItem] registerArmorMaterial called");
        event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
            System.out.println("[AdvancedSteelItem] Inside register callback");
            ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 4);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.CHESTPLATE, 10);
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.BODY, 10);
                System.out.println("[AdvancedSteelItem] Defense values set: BOOTS=4, LEGGINGS=7, CHESTPLATE=10, HELMET=4, BODY=10");
            }), 9, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")), () -> Ingredient.of(new ItemStack(MutModItems.ADVANCED_STEEL_INGOT.get())),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:advanced_steel"))), 3f, 0.15f);
            registerHelper.register(ResourceLocation.parse("mut:advanced_steel"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
            System.out.println("[AdvancedSteelItem] ArmorMaterial registered: " + ARMOR_MATERIAL);
        });
    }

    public AdvancedSteelItem(ArmorItem.Type type, Item.Properties properties) {
        super(ARMOR_MATERIAL, type, properties);
        System.out.println("[AdvancedSteelItem] Constructed " + type + ", ARMOR_MATERIAL = " + (ARMOR_MATERIAL != null ? ARMOR_MATERIAL.value() : "NULL"));
        System.out.println("[AdvancedSteelItem] Defense value for " + type + " = " + (ARMOR_MATERIAL != null ? ARMOR_MATERIAL.value().getDefense(type) : "N/A"));
        System.out.println("[AdvancedSteelItem] Properties = " + properties);
    }

    public static class Helmet extends AdvancedSteelItem {
        public Helmet() {
            super(ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(55)).fireResistant());
            System.out.println("[AdvancedSteelItem] Helmet created, durability multiplier = 55");
        }
    }

    public static class Chestplate extends AdvancedSteelItem {
        public Chestplate() {
            super(ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(55)).fireResistant());
            System.out.println("[AdvancedSteelItem] Chestplate created, durability multiplier = 55");
        }
    }

    public static class Leggings extends AdvancedSteelItem {
        public Leggings() {
            super(ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(55)).fireResistant());
            System.out.println("[AdvancedSteelItem] Leggings created, durability multiplier = 55");
        }
    }

    public static class Boots extends AdvancedSteelItem {
        public Boots() {
            super(ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(55)).fireResistant());
            System.out.println("[AdvancedSteelItem] Boots created, durability multiplier = 55");
        }
    }
}