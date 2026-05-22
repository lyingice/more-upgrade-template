package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.world.item.*;

public abstract class AmethystTools extends Item {
    public AmethystTools(Properties p) { super(p); }

    public static class Sword extends SwordItem {
        public Sword() {
            super(MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.SWORD),
                    new Item.Properties()
                            .attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.SWORD)));
        }
    }

    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.SHOVEL),
                    new Item.Properties()
                            .attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.SHOVEL)));
        }
    }

    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.PICKAXE),
                    new Item.Properties()
                            .attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.PICKAXE)));
        }
    }

    public static class Axe extends AxeItem {
        public Axe() {
            super(MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.AXE),
                    new Item.Properties()
                            .attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.AXE)));
        }
    }

    public static class Hoe extends HoeItem {
        public Hoe() {
            super(MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.HOE),
                    new Item.Properties()
                            .attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.HOE)));
        }
    }
}