package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public abstract class AmethystArmor extends ArmorItem {

    // 获取 Holder<ArmorMaterial>
    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
            net.minecraft.core.Registry.registerForHolder(
                    net.minecraft.core.registries.BuiltInRegistries.ARMOR_MATERIAL,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("mut", "amethyst"),
                    MutMaterials.AMETHYST.asArmorMaterial()
            );

    public AmethystArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends AmethystArmor {
        public Helmet() {
            super(Type.HELMET, new Item.Properties()
                    .durability(Type.HELMET.getDurability(MutMaterials.AMETHYST.armorDurabilityMultiplier()))
                    .attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, Type.HELMET)));
        }
    }

    public static class Chestplate extends AmethystArmor {
        public Chestplate() {
            super(Type.CHESTPLATE, new Item.Properties()
                    .durability(Type.CHESTPLATE.getDurability(MutMaterials.AMETHYST.armorDurabilityMultiplier()))
                    .attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, Type.CHESTPLATE)));
        }
    }

    public static class Leggings extends AmethystArmor {
        public Leggings() {
            super(Type.LEGGINGS, new Item.Properties()
                    .durability(Type.LEGGINGS.getDurability(MutMaterials.AMETHYST.armorDurabilityMultiplier()))
                    .attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, Type.LEGGINGS)));
        }
    }

    public static class Boots extends AmethystArmor {
        public Boots() {
            super(Type.BOOTS, new Item.Properties()
                    .durability(Type.BOOTS.getDurability(MutMaterials.AMETHYST.armorDurabilityMultiplier()))
                    .attributes(MutMaterials.createArmorAttributes(MutMaterials.AMETHYST, Type.BOOTS)));
        }
    }
}