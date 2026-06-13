package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

public abstract class UncannyAmethystTools extends Item {
    public UncannyAmethystTools(Properties p) { super(p); }

    public static class Sword extends SwordItem {
        public Sword() {
            super(MutMaterials.UNCANNY_AMETHYST.asToolTier(MutMaterials.ToolType.SWORD),
                    MutMaterials.UNCANNY_AMETHYST.createToolProperties(MutMaterials.ToolType.SWORD).component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(MutMaterials.UNCANNY_AMETHYST.asToolTier(MutMaterials.ToolType.SHOVEL),
                    MutMaterials.UNCANNY_AMETHYST.createToolProperties(MutMaterials.ToolType.SHOVEL).component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(MutMaterials.UNCANNY_AMETHYST.asToolTier(MutMaterials.ToolType.PICKAXE),
                    MutMaterials.UNCANNY_AMETHYST.createToolProperties(MutMaterials.ToolType.PICKAXE).component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class Axe extends AxeItem {
        public Axe() {
            super(MutMaterials.UNCANNY_AMETHYST.asToolTier(MutMaterials.ToolType.AXE),
                    MutMaterials.UNCANNY_AMETHYST.createToolProperties(MutMaterials.ToolType.AXE).component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class Hoe extends HoeItem {
        public Hoe() {
            super(MutMaterials.UNCANNY_AMETHYST.asToolTier(MutMaterials.ToolType.HOE),
                    MutMaterials.UNCANNY_AMETHYST.createToolProperties(MutMaterials.ToolType.HOE).component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
}
