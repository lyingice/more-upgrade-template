package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.mcreator.mut.item.armor.IChargedItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public abstract class ThunderCopperTools extends Item implements IChargedItem {

    private static final ResourceLocation ENERGY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mut", "textures/item/thunder_copper_energy.png");

    public ThunderCopperTools(Properties p) {
        super(p);
    }

    @Override
    public ResourceLocation getEnergyTexture() {
        return ENERGY_TEXTURE;
    }

    public static class Sword extends SwordItem implements IChargedItem {
        public Sword() {
            super(MutMaterials.THUNDER_COPPER.asToolTier(MutMaterials.ToolType.SWORD),
                    MutMaterials.THUNDER_COPPER.createToolProperties(MutMaterials.ToolType.SWORD));
        }

        @Override
        public ResourceLocation getEnergyTexture() {
            return ENERGY_TEXTURE;
        }
    }

    public static class Shovel extends ShovelItem implements IChargedItem {
        public Shovel() {
            super(MutMaterials.THUNDER_COPPER.asToolTier(MutMaterials.ToolType.SHOVEL),
                    MutMaterials.THUNDER_COPPER.createToolProperties(MutMaterials.ToolType.SHOVEL));
        }

        @Override
        public ResourceLocation getEnergyTexture() {
            return ENERGY_TEXTURE;
        }
    }

    public static class Pickaxe extends PickaxeItem implements IChargedItem {
        public Pickaxe() {
            super(MutMaterials.THUNDER_COPPER.asToolTier(MutMaterials.ToolType.PICKAXE),
                    MutMaterials.THUNDER_COPPER.createToolProperties(MutMaterials.ToolType.PICKAXE));
        }

        @Override
        public ResourceLocation getEnergyTexture() {
            return ENERGY_TEXTURE;
        }
    }

    public static class Axe extends AxeItem implements IChargedItem {
        public Axe() {
            super(MutMaterials.THUNDER_COPPER.asToolTier(MutMaterials.ToolType.AXE),
                    MutMaterials.THUNDER_COPPER.createToolProperties(MutMaterials.ToolType.AXE));
        }

        @Override
        public ResourceLocation getEnergyTexture() {
            return ENERGY_TEXTURE;
        }
    }

    public static class Hoe extends HoeItem implements IChargedItem {
        public Hoe() {
            super(MutMaterials.THUNDER_COPPER.asToolTier(MutMaterials.ToolType.HOE),
                    MutMaterials.THUNDER_COPPER.createToolProperties(MutMaterials.ToolType.HOE));
        }

        @Override
        public ResourceLocation getEnergyTexture() {
            return ENERGY_TEXTURE;
        }
    }
}