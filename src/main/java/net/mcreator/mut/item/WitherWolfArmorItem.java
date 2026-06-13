package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;

public class WitherWolfArmorItem extends BaseAnimalArmorItem {
        public WitherWolfArmorItem() {
            super(AnimalArmorMaterials.WITHER, BodyType.EQUESTRIAN, false,props -> props.component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "wither_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }

}