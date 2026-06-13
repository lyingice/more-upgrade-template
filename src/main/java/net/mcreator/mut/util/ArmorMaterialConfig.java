package net.mcreator.mut.util;

import com.google.gson.JsonObject;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class ArmorMaterialConfig {
    public final String name;
    public final int bootsDefense;
    public final int leggingsDefense;
    public final int chestplateDefense;
    public final int helmetDefense;
    public final int bodyDefense;
    public final int enchantmentValue;
    public final float toughness;
    public final float knockbackResistance;
    public final Supplier<Ingredient> repairIngredientSupplier;
    public final ResourceLocation textureLocation;
    public final Holder<SoundEvent> equipSound;
    public final int durabilityMultiplier;

    public ArmorMaterialConfig(String name, int boots, int leggings, int chestplate, int helmet, int body,
                               int enchantValue, float toughness, float knockbackRes,
                               Supplier<Ingredient> repairIngredientSupplier,
                               ResourceLocation texture, Holder<SoundEvent> sound,
                               int durabilityMultiplier) {
        this.name = name;
        this.bootsDefense = boots;
        this.leggingsDefense = leggings;
        this.chestplateDefense = chestplate;
        this.helmetDefense = helmet;
        this.bodyDefense = body;
        this.enchantmentValue = enchantValue;
        this.toughness = toughness;
        this.knockbackResistance = knockbackRes;
        this.repairIngredientSupplier = repairIngredientSupplier;
        this.textureLocation = texture;
        this.equipSound = sound;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    public static ArmorMaterialConfig fromJson(String name, JsonObject obj) {
        System.out.println("[ArmorConfig] Parsing config for: " + name);
        System.out.println("[ArmorConfig] Raw JSON: " + obj.toString());

        int boots = obj.has("boots") ? obj.get("boots").getAsInt() : 3;
        System.out.println("[ArmorConfig] Parsed boots: " + boots);
        int leggings = obj.has("leggings") ? obj.get("leggings").getAsInt() : 6;
        int chestplate = obj.has("chestplate") ? obj.get("chestplate").getAsInt() : 8;
        int helmet = obj.has("helmet") ? obj.get("helmet").getAsInt() : 3;
        int body = obj.has("body") ? obj.get("body").getAsInt() : 8;
        int enchantValue = obj.has("enchantment_value") ? obj.get("enchantment_value").getAsInt() : 9;
        float toughness = obj.has("toughness") ? obj.get("toughness").getAsFloat() : 2.5f;
        float knockbackRes = obj.has("knockback_resistance") ? obj.get("knockback_resistance").getAsFloat() : 0.1f;
        int durabilityMultiplier = obj.has("durability_multiplier") ? obj.get("durability_multiplier").getAsInt() : 100;

        String repairItem = obj.has("repair_ingredient") ? obj.get("repair_ingredient").getAsString() : "minecraft:iron_ingot";
        Supplier<Ingredient> repairIngredientSupplier = () -> Ingredient.of(
                BuiltInRegistries.ITEM.get(ResourceLocation.parse(repairItem))
        );

        String texture = obj.has("texture") ? obj.get("texture").getAsString() : "steel";
        ResourceLocation textureLocation = ResourceLocation.parse("mut:" + texture);

        String soundId = obj.has("equip_sound") ? obj.get("equip_sound").getAsString() : "item.armor.equip_netherite";
        Holder<SoundEvent> equipSound = BuiltInRegistries.SOUND_EVENT.getHolder(
                ResourceLocation.parse(soundId)).orElseGet(() -> (Holder.Reference<SoundEvent>) SoundEvents.ARMOR_EQUIP_NETHERITE);

        return new ArmorMaterialConfig(name, boots, leggings, chestplate, helmet, body,
                enchantValue, toughness, knockbackRes, repairIngredientSupplier, textureLocation, equipSound,
                durabilityMultiplier);
    }

    // 获取防御值的便捷方法
    public int getDefense(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> bootsDefense;
            case LEGGINGS -> leggingsDefense;
            case CHESTPLATE -> chestplateDefense;
            case HELMET -> helmetDefense;
            case BODY -> bodyDefense;
            default -> 0;
        };
    }

    public static ArmorMaterialConfig getHardcodedDefault(String name) {
        switch (name) {
            case "steel":
                return new ArmorMaterialConfig(name,
                        3, 6, 8, 3, 8,
                        9, 2.5f, 0.1f,
                        () -> Ingredient.of(Items.IRON_INGOT),
                        ResourceLocation.parse("mut:steel"),
                        SoundEvents.ARMOR_EQUIP_NETHERITE,
                        35
                );

            case "crying_obsidian":
                return new ArmorMaterialConfig(name,
                        5, 10, 12, 5, 12,
                        1, 4f, 0.2f,
                        () -> Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT.get()),
                        ResourceLocation.parse("mut:crying_obsidian"),
                        SoundEvents.ARMOR_EQUIP_NETHERITE,
                        100
                );

            case "dragon":
                return new ArmorMaterialConfig(name,
                        5, 9, 11, 5, 11,
                        30, 4f, 0.1f,
                        () -> Ingredient.of(Items.NETHER_STAR),
                        ResourceLocation.parse("mut:dragon"),
                        SoundEvents.ARMOR_EQUIP_NETHERITE,
                        68
                );

            case "nether_star":
                return new ArmorMaterialConfig(name,
                        3, 6, 8, 3, 8,
                        22, 10f, 0.1f,
                        () -> Ingredient.of(Items.NETHER_STAR),
                        ResourceLocation.parse("mut:nether_star"),
                        SoundEvents.ARMOR_EQUIP_NETHERITE,
                        88
                );

            default:
                return new ArmorMaterialConfig(name,
                        2, 5, 6, 2, 6,
                        9, 0.5f, 0f,
                        () -> Ingredient.of(Items.IRON_INGOT),
                        ResourceLocation.parse("mut:" + name),
                        SoundEvents.ARMOR_EQUIP_IRON,
                        21
                );
        }
    }
}