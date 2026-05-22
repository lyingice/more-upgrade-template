package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.world.item.Item;

public class SteelWolfArmorItem extends BaseAnimalArmorItem {
    public SteelWolfArmorItem() {
        super(AnimalArmorMaterials.STEEL, BodyType.EQUESTRIAN, false);
    }
}