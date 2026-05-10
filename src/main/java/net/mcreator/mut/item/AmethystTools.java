package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.world.item.*;

public abstract class AmethystTools extends Item {
    public AmethystTools(Properties p) { super(p); }

    public static class Sword extends SwordItem {
        private static final Tier TIER = MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.SWORD);
        public Sword() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.SWORD))); }
    }

    public static class Shovel extends ShovelItem {
        private static final Tier TIER = MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.SHOVEL);
        public Shovel() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.SHOVEL))); }
    }

    public static class Pickaxe extends PickaxeItem {
        private static final Tier TIER = MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.PICKAXE);
        public Pickaxe() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.PICKAXE))); }
    }

    public static class Axe extends AxeItem {
        private static final Tier TIER = MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.AXE);
        public Axe() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.AXE))); }
    }

    public static class Hoe extends HoeItem {
        private static final Tier TIER = MutMaterials.AMETHYST.asToolTier(MutMaterials.ToolType.HOE);
        public Hoe() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, MutMaterials.ToolType.HOE))); }
    }
}