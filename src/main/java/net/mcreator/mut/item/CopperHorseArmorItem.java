package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;

public class CopperHorseArmorItem extends AnimalArmorItem {
    public CopperHorseArmorItem() {
        super(
                AnimalArmorMaterials.COPPER.material(),
                AnimalArmorItem.BodyType.EQUESTRIAN,
                false,
                new Item.Properties()
                        .stacksTo(1)
                        .attributes(AnimalArmorMaterials.createAttributes(AnimalArmorMaterials.COPPER))
        );
    }
    @Override
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/copper_horse_armor.png");
    }
}