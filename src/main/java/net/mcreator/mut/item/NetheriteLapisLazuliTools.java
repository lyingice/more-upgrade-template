package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.world.item.*;

public abstract class NetheriteLapisLazuliTools extends Item {
    public NetheriteLapisLazuliTools(Properties p) { super(p); }

    public static class Sword extends SwordItem {
        public Sword() {
            super(MutMaterials.NETHERITE_LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.SWORD),
                    MutMaterials.NETHERITE_LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.SWORD));
        }
    }
    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(MutMaterials.NETHERITE_LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.SHOVEL),
                    MutMaterials.NETHERITE_LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.SHOVEL));
        }
    }
    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(MutMaterials.NETHERITE_LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.PICKAXE),
                    MutMaterials.NETHERITE_LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.PICKAXE));
        }
    }
    public static class Axe extends AxeItem {
        public Axe() {
            super(MutMaterials.NETHERITE_LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.AXE),
                    MutMaterials.NETHERITE_LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.AXE));
        }
    }
    public static class Hoe extends HoeItem {
        public Hoe() {
            super(MutMaterials.NETHERITE_LAPIS_LAZULI.asToolTier(MutMaterials.ToolType.HOE),
                    MutMaterials.NETHERITE_LAPIS_LAZULI.createToolProperties(MutMaterials.ToolType.HOE));
        }
    }
}
