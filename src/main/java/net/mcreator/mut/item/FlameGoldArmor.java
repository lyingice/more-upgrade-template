package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

public abstract class FlameGoldArmor extends ArmorItem {

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
        net.minecraft.core.Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath("mut", "flame_gold"),
            MutMaterials.FLAME_GOLD.asArmorMaterial()
        );
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "fire_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }

    public FlameGoldArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    public static class Helmet extends FlameGoldArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.FLAME_GOLD.createArmorProperties(Type.HELMET).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Chestplate extends FlameGoldArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.FLAME_GOLD.createArmorProperties(Type.CHESTPLATE).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Leggings extends FlameGoldArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.FLAME_GOLD.createArmorProperties(Type.LEGGINGS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
    public static class Boots extends FlameGoldArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.FLAME_GOLD.createArmorProperties(Type.BOOTS).component(DataComponents.CUSTOM_DATA, createWitherMarkData())); }
    }
}
