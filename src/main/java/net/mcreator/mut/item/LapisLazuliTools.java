package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.world.item.*;

public abstract class LapisLazuliTools extends Item {
    public LapisLazuliTools(Properties p) { super(p); }

    public static class Sword extends SwordItem {
        public Sword() {
            super(MutMaterials.LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.SWORD),
                    MutMaterials.LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.SWORD));
        }
    }
    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(MutMaterials.LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.SHOVEL),
                    MutMaterials.LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.SHOVEL));
        }
    }
    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(MutMaterials.LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.PICKAXE),
                    MutMaterials.LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.PICKAXE));
        }
    }
    public static class Axe extends AxeItem {
        public Axe() {
            super(MutMaterials.LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.AXE),
                    MutMaterials.LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.AXE));
        }
    }
    public static class Hoe extends HoeItem {
        public Hoe() {
            super(MutMaterials.LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.HOE),
                    MutMaterials.LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.HOE));
        }
    }
}
