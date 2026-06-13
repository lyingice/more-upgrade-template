package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

public abstract class PositionSteelArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "position_steel"),
            MutMaterials.POSITION_STEEL.asArmorMaterial()
        );
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "poison_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }

    public PositionSteelArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends PositionSteelArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.POSITION_STEEL.createArmorProperties(Type.HELMET).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Chestplate extends PositionSteelArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.POSITION_STEEL.createArmorProperties(Type.CHESTPLATE).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Leggings extends PositionSteelArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.POSITION_STEEL.createArmorProperties(Type.LEGGINGS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Boots extends PositionSteelArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.POSITION_STEEL.createArmorProperties(Type.BOOTS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
}
