package net.mcreator.mut.init;

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
import java.util.List;
import java.util.function.Supplier;

public class AnimalArmorMaterials {

    /**
     * 自定义记录：额外存储动物铠甲的特殊属性
     */
    public record AnimalArmorStats(
            Holder<ArmorMaterial> material,
            double maxHealthBonus,      // 最大生命值加成
            double movementSpeedBonus   // 移动速度加成
    ) {}

    // ========== 原版材料 ==========
    public static final AnimalArmorStats COPPER = registerAnimal(
            "copper_animal", 5, 9,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.of(Items.COPPER_INGOT),
            0.0D, 0.1D  //
    );
    public static final AnimalArmorStats IRON_ANIMAL = registerAnimal(
            "iron_animal", 5, 9,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.of(Items.IRON_INGOT),
            0.0D, 0.0D  // 左边生命右边移速
    );

    public static final AnimalArmorStats GOLDEN_ANIMAL = registerAnimal(
            "golden_animal", 7, 25,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F,
            () -> Ingredient.of(Items.GOLD_INGOT),
            2.0D, 0.1D  //
    );

    public static final AnimalArmorStats DIAMOND_ANIMAL = registerAnimal(
            "diamond_animal", 11, 10,
            SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F,
            () -> Ingredient.of(Items.DIAMOND),
            4.0D, 0.15D  //
    );

    public static final AnimalArmorStats NETHERITE_ANIMAL = registerAnimal(
            "netherite_animal", 19, 15,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT),
            5.0D, 0.25D  // +3颗心，+5%移速
    );

    // ========== 自定义材料（后续追加） ==========
    // public static final AnimalArmorStats STEEL_ANIMAL = registerAnimal(...);

    // ========== 工厂方法 ==========
    private static AnimalArmorStats registerAnimal(
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

        return new AnimalArmorStats(material, maxHealthBonus, movementSpeedBonus);
    }

    /**
     * 根据 AnimalArmorStats 构建属性修饰符
     * 在物品构造时调用
     */
    public static ItemAttributeModifiers createAttributes(AnimalArmorStats stats) {
        var builder = ItemAttributeModifiers.builder();
        String path = stats.material.unwrapKey().orElseThrow().location().getPath();
        double armorValue = stats.material.value().getDefense(ArmorItem.Type.BODY);
        double toughness = stats.material.value().toughness();
        double knockbackRes = stats.material.value().knockbackResistance();

        // 护甲值
        builder.add(Attributes.ARMOR,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("mut", path + "_armor"),
                        armorValue,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.BODY);

        // 盔甲韧性
        if (toughness > 0) {
            builder.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_toughness"),
                            toughness,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        // 击退抗性
        if (knockbackRes > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_knockback"),
                            knockbackRes,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        // 生命值
        if (stats.maxHealthBonus > 0) {
            builder.add(Attributes.MAX_HEALTH,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mut", path + "_health"),
                            stats.maxHealthBonus,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.BODY);
        }

        // 移动速度
        if (stats.movementSpeedBonus > 0) {
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
}