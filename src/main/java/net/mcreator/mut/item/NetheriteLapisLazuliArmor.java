package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public abstract class NetheriteLapisLazuliArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "netherite_lapis_lazuli"),
            MutMaterials.NETHERITE_LAPIS_LAZULI.asArmorMaterial()
        );

    public NetheriteLapisLazuliArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends NetheriteLapisLazuliArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.NETHERITE_LAPIS_LAZULI.createArmorProperties(Type.HELMET)); }
    }
    public static class Chestplate extends NetheriteLapisLazuliArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.NETHERITE_LAPIS_LAZULI.createArmorProperties(Type.CHESTPLATE)); }
    }
    public static class Leggings extends NetheriteLapisLazuliArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.NETHERITE_LAPIS_LAZULI.createArmorProperties(Type.LEGGINGS)); }
    }
    public static class Boots extends NetheriteLapisLazuliArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.NETHERITE_LAPIS_LAZULI.createArmorProperties(Type.BOOTS)); }
    }
}
