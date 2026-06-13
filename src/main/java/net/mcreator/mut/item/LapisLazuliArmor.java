package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public abstract class LapisLazuliArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "lapis_lazuli"),
            MutMaterials.LAPIS_LAZULI.asArmorMaterial()
        );

    public LapisLazuliArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends LapisLazuliArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.LAPIS_LAZULI.createArmorProperties(Type.HELMET)); }
    }
    public static class Chestplate extends LapisLazuliArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.LAPIS_LAZULI.createArmorProperties(Type.CHESTPLATE)); }
    }
    public static class Leggings extends LapisLazuliArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.LAPIS_LAZULI.createArmorProperties(Type.LEGGINGS)); }
    }
    public static class Boots extends LapisLazuliArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.LAPIS_LAZULI.createArmorProperties(Type.BOOTS)); }
    }
}
