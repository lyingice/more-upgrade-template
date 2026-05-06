package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;

public class NetheriteHorseArmorItem extends AnimalArmorItem {
    public NetheriteHorseArmorItem() {
        super(
                AnimalArmorMaterials.NETHERITE_ANIMAL.material(),
                AnimalArmorItem.BodyType.EQUESTRIAN,
                false,
                new Item.Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .attributes(AnimalArmorMaterials.createAttributes(AnimalArmorMaterials.NETHERITE_ANIMAL))
        );
    }
    @Override
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_horse_armor.png");
    }
}