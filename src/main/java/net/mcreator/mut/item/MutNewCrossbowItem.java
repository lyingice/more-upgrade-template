package net.mcreator.mut.item;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutCrossbowStats;

public class MutNewCrossbowItem {

    public static class IronCrossbowItem extends NewCrossbowItem {
        public IronCrossbowItem() {
            super(MutModItems.IRON_CROSSBOW::get, 590, false);
        }
    }

    public static class GoldenCrossbowItem extends NewCrossbowItem {
        public GoldenCrossbowItem() {
            super(MutModItems.GOLDEN_CROSSBOW::get, 715, false);
        }
    }

    public static class DiamondCrossbowItem extends NewCrossbowItem {
        public DiamondCrossbowItem() {
            super(MutModItems.DIAMOND_CROSSBOW::get, 1246, false);
        }
    }

    public static class NetheriteCrossbowItem extends NewCrossbowItem {
        public NetheriteCrossbowItem() {
            super(MutModItems.NETHERITE_CROSSBOW::get, 1481, true);
        }
    }
}