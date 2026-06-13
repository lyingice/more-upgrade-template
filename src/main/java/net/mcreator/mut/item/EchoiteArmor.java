package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public abstract class EchoiteArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "echoite"),
            MutMaterials.ECHOITE.asArmorMaterial()
        );

    public EchoiteArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends EchoiteArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.ECHOITE.createArmorProperties(Type.HELMET)); }
    }
    public static class Chestplate extends EchoiteArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.ECHOITE.createArmorProperties(Type.CHESTPLATE)); }
    }
    public static class Leggings extends EchoiteArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.ECHOITE.createArmorProperties(Type.LEGGINGS)); }
    }
    public static class Boots extends EchoiteArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.ECHOITE.createArmorProperties(Type.BOOTS)); }
    }
}
