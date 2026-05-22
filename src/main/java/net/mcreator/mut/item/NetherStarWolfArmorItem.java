package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class NetherStarWolfArmorItem extends BaseAnimalArmorItem {
        public NetherStarWolfArmorItem() {
            super(AnimalArmorMaterials.NETHER_STAR, BodyType.EQUESTRIAN, false);
        }
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }
}