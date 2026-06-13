package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.mcreator.mut.item.armor.IChargedArmor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public abstract class ThunderCopperArmor extends ArmorItem implements IChargedArmor {

    private static final ResourceLocation ENERGY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mut", "textures/armor/thunder_copper_energy.png");

    private static final Holder<ArmorMaterial> ARMOR_MATERIAL_HOLDER =
            net.minecraft.core.Registry.registerForHolder(
                    BuiltInRegistries.ARMOR_MATERIAL,
                    ResourceLocation.fromNamespaceAndPath("mut", "thunder_copper"),
                    MutMaterials.THUNDER_COPPER.asArmorMaterial()
            );

    public ThunderCopperArmor(Type type, Properties properties) {
        super(ARMOR_MATERIAL_HOLDER, type, properties);
    }

    @Override
    public ResourceLocation getEnergyTexture() {
        return ENERGY_TEXTURE;
    }

    public static class Helmet extends ThunderCopperArmor {
        public Helmet() { super(Type.HELMET, MutMaterials.THUNDER_COPPER.createArmorProperties(Type.HELMET)); }
    }
    public static class Chestplate extends ThunderCopperArmor {
        public Chestplate() { super(Type.CHESTPLATE, MutMaterials.THUNDER_COPPER.createArmorProperties(Type.CHESTPLATE)); }
    }
    public static class Leggings extends ThunderCopperArmor {
        public Leggings() { super(Type.LEGGINGS, MutMaterials.THUNDER_COPPER.createArmorProperties(Type.LEGGINGS)); }
    }
    public static class Boots extends ThunderCopperArmor {
        public Boots() { super(Type.BOOTS, MutMaterials.THUNDER_COPPER.createArmorProperties(Type.BOOTS)); }
    }
}