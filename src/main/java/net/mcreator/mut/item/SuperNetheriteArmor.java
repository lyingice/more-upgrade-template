package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

public abstract class SuperNetheriteArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "super_netherite"),
            MutMaterials.SUPER_NETHERITE.asArmorMaterial()
        );

    public SuperNetheriteArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "momentum");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }

    public static class Helmet extends SuperNetheriteArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.SUPER_NETHERITE.createArmorProperties(Type.HELMET).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Chestplate extends SuperNetheriteArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.SUPER_NETHERITE.createArmorProperties(Type.CHESTPLATE).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Leggings extends SuperNetheriteArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.SUPER_NETHERITE.createArmorProperties(Type.LEGGINGS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Boots extends SuperNetheriteArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.SUPER_NETHERITE.createArmorProperties(Type.BOOTS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
}
