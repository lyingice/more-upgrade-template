package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

public abstract class UncannyAmethystArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "uncanny_amethyst"),
            MutMaterials.UNCANNY_AMETHYST.asArmorMaterial()
        );
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "regeneration_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }

    public UncannyAmethystArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends UncannyAmethystArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.UNCANNY_AMETHYST.createArmorProperties(Type.HELMET).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Chestplate extends UncannyAmethystArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.UNCANNY_AMETHYST.createArmorProperties(Type.CHESTPLATE).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Leggings extends UncannyAmethystArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.UNCANNY_AMETHYST.createArmorProperties(Type.LEGGINGS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Boots extends UncannyAmethystArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.UNCANNY_AMETHYST.createArmorProperties(Type.BOOTS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
}
