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

public abstract class AmethystArmor extends ArmorItem {

    public static final Holder<ArmorMaterial> ARMOR_MATERIAL =
            net.minecraft.core.Registry.registerForHolder(
                    BuiltInRegistries.ARMOR_MATERIAL,
                    ResourceLocation.fromNamespaceAndPath("mut", "amethyst"),
                    new ArmorMaterial(
                            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                                map.put(ArmorItem.Type.BOOTS, 2);
                                map.put(ArmorItem.Type.LEGGINGS, 5);
                                map.put(ArmorItem.Type.CHESTPLATE, 6);
                                map.put(ArmorItem.Type.HELMET, 2);
                                map.put(ArmorItem.Type.BODY, 5);
                            }),
                            16,
                            SoundEvents.ARMOR_EQUIP_IRON,
                            () -> Ingredient.of(),
                            List.of(new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath("mut", "amethyst")
                            )),
                            0.5f,
                            0.00f
                    )
            );

    public AmethystArmor(ArmorItem.Type type, Item.Properties properties) { super(ARMOR_MATERIAL, type, properties); }

    public static class Helmet extends AmethystArmor {
        public Helmet() { super(ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(21)).attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, ArmorItem.Type.HELMET))); }
    }
    public static class Chestplate extends AmethystArmor {
        public Chestplate() { super(ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(21)).attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, ArmorItem.Type.CHESTPLATE))); }
    }
    public static class Leggings extends AmethystArmor {
        public Leggings() { super(ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(21)).attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, ArmorItem.Type.LEGGINGS))); }
    }
    public static class Boots extends AmethystArmor {
        public Boots() { super(ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(21)).attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, ArmorItem.Type.BOOTS))); }
    }
}
