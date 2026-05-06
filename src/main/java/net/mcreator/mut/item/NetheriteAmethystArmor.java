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

public abstract class NetheriteAmethystArmor extends ArmorItem {

    public static final Holder<ArmorMaterial> ARMOR_MATERIAL =
            net.minecraft.core.Registry.registerForHolder(
                    BuiltInRegistries.ARMOR_MATERIAL,
                    ResourceLocation.fromNamespaceAndPath("mut", "netherite_amethyst"),
                    new ArmorMaterial(
                            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                                map.put(ArmorItem.Type.BOOTS, 3);
                                map.put(ArmorItem.Type.LEGGINGS, 6);
                                map.put(ArmorItem.Type.CHESTPLATE, 8);
                                map.put(ArmorItem.Type.HELMET, 3);
                                map.put(ArmorItem.Type.BODY, 0);
                            }),
                            14,
                            SoundEvents.ARMOR_EQUIP_NETHERITE,
                            () -> Ingredient.of(),
                            List.of(new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath("mut", "netherite_amethyst")
                            )),
                            3.5f,
                            0.10f
                    )
            );

    public NetheriteAmethystArmor(ArmorItem.Type type, Item.Properties properties) { super(ARMOR_MATERIAL, type, properties); }

    public static class Helmet extends NetheriteAmethystArmor {
        public Helmet() { super(ArmorItem.Type.HELMET, new Item.Properties()
                                    .fireResistant().durability(ArmorItem.Type.HELMET.getDurability(42)).attributes(MutMaterials.createArmorAttributes(MutMaterials.NETHERITE_AMETHYST, ArmorItem.Type.HELMET))); }
    }
    public static class Chestplate extends NetheriteAmethystArmor {
        public Chestplate() { super(ArmorItem.Type.CHESTPLATE, new Item.Properties()
                                    .fireResistant().durability(ArmorItem.Type.CHESTPLATE.getDurability(42)).attributes(MutMaterials.createArmorAttributes(MutMaterials.NETHERITE_AMETHYST, ArmorItem.Type.CHESTPLATE))); }
    }
    public static class Leggings extends NetheriteAmethystArmor {
        public Leggings() { super(ArmorItem.Type.LEGGINGS, new Item.Properties()
                                    .fireResistant().durability(ArmorItem.Type.LEGGINGS.getDurability(42)).attributes(MutMaterials.createArmorAttributes(MutMaterials.NETHERITE_AMETHYST, ArmorItem.Type.LEGGINGS))); }
    }
    public static class Boots extends NetheriteAmethystArmor {
        public Boots() { super(ArmorItem.Type.BOOTS, new Item.Properties()
                                    .fireResistant().durability(ArmorItem.Type.BOOTS.getDurability(42)).attributes(MutMaterials.createArmorAttributes(MutMaterials.NETHERITE_AMETHYST, ArmorItem.Type.BOOTS))); }
    }
}
