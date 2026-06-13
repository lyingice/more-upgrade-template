package net.mcreator.mut.item;

import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.apache.http.config.Registry;
import org.jetbrains.annotations.NotNull;


public abstract class WitherTools extends Item {
    public WitherTools(Properties p) { super(p); }
    private static final ResourceKey<Enchantment> WITHER_ASPECT_KEY =
            ResourceKey.create(
                    Registries.ENCHANTMENT,
                    ResourceLocation.fromNamespaceAndPath("mut", "wither_aspect")
            );
    private static CustomData createWitherMarkData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "wither_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);
    }
    public static class Sword extends SwordItem {
        public Sword() {
            super(MutMaterials.WITHER.asToolTier(MutMaterials.ToolType.SWORD),
                    MutMaterials.WITHER.createToolProperties(MutMaterials.ToolType.SWORD)
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
    }
    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(MutMaterials.WITHER.asToolTier(MutMaterials.ToolType.SHOVEL),
                    MutMaterials.WITHER.createToolProperties(MutMaterials.ToolType.SHOVEL)
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
    }
    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(MutMaterials.WITHER.asToolTier(MutMaterials.ToolType.PICKAXE),
                    MutMaterials.WITHER.createToolProperties(MutMaterials.ToolType.PICKAXE)
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
    }
    public static class Axe extends AxeItem {
        public Axe() {
            super(MutMaterials.WITHER.asToolTier(MutMaterials.ToolType.AXE),
                    MutMaterials.WITHER.createToolProperties(MutMaterials.ToolType.AXE)
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
    }
    public static class Hoe extends HoeItem {
        public Hoe() {
            super(MutMaterials.WITHER.asToolTier(MutMaterials.ToolType.HOE),
                    MutMaterials.WITHER.createToolProperties(MutMaterials.ToolType.HOE)
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
    }
}
