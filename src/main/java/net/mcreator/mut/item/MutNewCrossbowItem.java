package net.mcreator.mut.item;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutCrossbowStats;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MutNewCrossbowItem {

    public static class CopperCrossbowItem extends NewCrossbowItem {
        public CopperCrossbowItem() {
            super(MutModItems.COPPER_CROSSBOW::get, MutCrossbowStats.COPPER);
        }
    }

    public static class IronCrossbowItem extends NewCrossbowItem {
        public IronCrossbowItem() {
            super(MutModItems.IRON_CROSSBOW::get, MutCrossbowStats.IRON);
        }
    }

    public static class GoldenCrossbowItem extends NewCrossbowItem {
        public GoldenCrossbowItem() {
            super(MutModItems.GOLDEN_CROSSBOW::get, MutCrossbowStats.GOLDEN);
        }
    }

    public static class DiamondCrossbowItem extends NewCrossbowItem {
        public DiamondCrossbowItem() {
            super(MutModItems.DIAMOND_CROSSBOW::get, MutCrossbowStats.DIAMOND);
        }
    }

    public static class NetheriteCrossbowItem extends NewCrossbowItem {
        public NetheriteCrossbowItem() {
            super(MutModItems.NETHERITE_CROSSBOW::get, MutCrossbowStats.NETHERITE);
        }
    }

    public static class SteelCrossbowItem extends NewCrossbowItem {
        public SteelCrossbowItem() {
            super(MutModItems.STEEL_CROSSBOW::get, MutCrossbowStats.STEEL);
        }
    }

    public static class GilingCrossbowItem extends NewCrossbowItem {
        public GilingCrossbowItem() {
            super(MutModItems.GILDING_CROSSBOW::get, MutCrossbowStats.GILDING);
        }
    }

    public static class BlueDiamondCrossbowItem extends NewCrossbowItem {
        public BlueDiamondCrossbowItem() {
            super(MutModItems.BLUE_DIAMOND_CROSSBOW::get, MutCrossbowStats.BLUE_DIAMOND);
        }
    }

    public static class AdvancedSteelCrossbowItem extends NewCrossbowItem {
        public AdvancedSteelCrossbowItem() {
            super(MutModItems.ADVANCED_STEEL_CROSSBOW::get, MutCrossbowStats.ADVANCED_STEEL);
        }
    }

    public static class ObsidianCrossbowItem extends NewCrossbowItem {
        public ObsidianCrossbowItem() {
            super(MutModItems.OBSIDIAN_CROSSBOW::get, MutCrossbowStats.OBSIDIAN);
        }
    }

    public static class NetheriteObsidianCrossbowItem extends NewCrossbowItem {
        public NetheriteObsidianCrossbowItem() {
            super(MutModItems.NETHERITE_OBSIDIAN_CROSSBOW::get, MutCrossbowStats.NETHERITE_OBSIDIAN);
        }
    }

    public static class CryingObsidianCrossbowItem extends NewCrossbowItem {
        public CryingObsidianCrossbowItem() {
            super(MutModItems.CRYING_OBSIDIAN_CROSSBOW::get, MutCrossbowStats.CRYING_OBSIDIAN);
        }
    }

    public static class NetherStarCrossbowItem extends NewCrossbowItem {
        public NetherStarCrossbowItem() {
            super(MutModItems.NETHER_STAR_CROSSBOW::get, MutCrossbowStats.NETHER_STAR);
        }
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }

    }

    public static class WitherCrossbowItem extends NewCrossbowItem {
        public WitherCrossbowItem() {
            super(
                    MutModItems.WITHER_CROSSBOW::get,
                    MutCrossbowStats.WITHER,
                    new Properties()
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData())
            );
        }

        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "wither_mark");
            return CustomData.of(tag);
        }
    }

    public static class DragonCrossbowItem extends NewCrossbowItem {
        public DragonCrossbowItem() {
            super(MutModItems.DRAGON_CROSSBOW::get, MutCrossbowStats.DRAGON);
        }
    }
    // MutNewCrossbowItem.java 中添加子类
    public static class NetheriteEmeraldCrossbowItem extends NewCrossbowItem {
        public NetheriteEmeraldCrossbowItem() {
            super(MutModItems.NETHERITE_EMERALD_CROSSBOW::get, MutCrossbowStats.NETHERITE_EMERALD);
        }
    }
    public static class NetheriteRedstoneCrossbowItem extends NewCrossbowItem {
        public NetheriteRedstoneCrossbowItem() {
            super(MutModItems.NETHERITE_REDSTONE_CROSSBOW::get, MutCrossbowStats.NETHERITE_REDSTONE);
        }
    }
    public static class NetheriteAmethystCrossbowItem extends NewCrossbowItem {
        public NetheriteAmethystCrossbowItem() {
            super(MutModItems.NETHERITE_AMETHYST_CROSSBOW::get, MutCrossbowStats.NETHERITE_AMETHYST);
        }
    }
    public static class NetheriteCopperCrossbowItem extends NewCrossbowItem {
        public NetheriteCopperCrossbowItem() {
            super(MutModItems.NETHERITE_COPPER_CROSSBOW::get, MutCrossbowStats.NETHERITE_COPPER);
        }
    }
    public static class EmeraldCrossbowItem extends NewCrossbowItem {
        public EmeraldCrossbowItem() {
            super(MutModItems.EMERALD_CROSSBOW::get, MutCrossbowStats.EMERALD);
        }
    }
    public static class AmethystCrossbowItem extends NewCrossbowItem {
        public AmethystCrossbowItem() {
            super(MutModItems.AMETHYST_CROSSBOW::get, MutCrossbowStats.AMETHYST);
        }
    }
    /*public static class XxCrossbowItem extends NewCrossbowItem {
        public XxCrossbowItem() {
            super(MutModItems.X_CROSSBOW::get, MutCrossbowStats.X,new Properties());
        }
    }*/
}