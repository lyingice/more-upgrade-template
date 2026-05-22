package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DiamondWolfArmorItem extends AnimalArmorItem {
    public DiamondWolfArmorItem() {
        super(
                AnimalArmorMaterials.DIAMOND_ANIMAL.material(),
                AnimalArmorItem.BodyType.CANINE,
                false,
                new Item.Properties()
                        .stacksTo(1)
                        .durability(323)
                        .attributes(AnimalArmorMaterials.createAttributes(AnimalArmorMaterials.DIAMOND_ANIMAL))
        );
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true; // 默认不可附魔！
    }
}