package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GoldenWolfArmorItem extends AnimalArmorItem {
    public GoldenWolfArmorItem() {
        super(
                AnimalArmorMaterials.GOLDEN_ANIMAL.material(),
                AnimalArmorItem.BodyType.CANINE,
                false,
                new Item.Properties()
                        .stacksTo(1)
                        .durability(165)
                        .attributes(AnimalArmorMaterials.createAttributes(AnimalArmorMaterials.GOLDEN_ANIMAL))
        );
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true; // 默认不可附魔！
    }
}