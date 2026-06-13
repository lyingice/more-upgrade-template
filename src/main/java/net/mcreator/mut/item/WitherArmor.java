package net.mcreator.mut.item;

import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

public abstract class WitherArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "wither"),
            MutMaterials.get("wither").asArmorMaterial()
        );

    public WitherArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "wither_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }
    public static class Helmet extends WitherArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.get("wither").createArmorProperties(Type.HELMET).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Chestplate extends WitherArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.get("wither").createArmorProperties(Type.CHESTPLATE).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Leggings extends WitherArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.get("wither").createArmorProperties(Type.LEGGINGS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Boots extends WitherArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.get("wither").createArmorProperties(Type.BOOTS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
}
