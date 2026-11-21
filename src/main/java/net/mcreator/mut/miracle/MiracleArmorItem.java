package net.mcreator.mut.miracle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

/**
 * 奇迹装备（头盔 / 胸甲 / 护腿 / 靴子）。
 * <p>
 * 数值是占位；穿戴外观由客户端每 tick 现算的动态贴图提供（见 MiracleArmorGlow），
 * 所以在身上也能像物品图标那样流动变色。
 */
public abstract class MiracleArmorItem extends ArmorItem {

    protected MiracleArmorItem(ArmorItem.Type type) {
        super(MiracleArmorMaterial.MATERIAL, type, new Item.Properties()
                .durability(type.getDurability(MiracleArmorMaterial.DURABILITY_MULTIPLIER))
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 只在客户端生效：把盔甲层贴图换成每 tick 重新着色的动态贴图。
     * 返回 null 时游戏会回落到 ArmorMaterial 里登记的静态贴图。
     */
    @Nullable
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                            ArmorMaterial.Layer layer, boolean innerModel) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return null;
        }
        return net.mcreator.mut.miracle.client.MiracleArmorGlow.texture(innerModel);
    }

    public static class Helmet extends MiracleArmorItem {
        public Helmet() {
            super(ArmorItem.Type.HELMET);
        }
    }

    public static class Chestplate extends MiracleArmorItem {
        public Chestplate() {
            super(ArmorItem.Type.CHESTPLATE);
        }
    }

    public static class Leggings extends MiracleArmorItem {
        public Leggings() {
            super(ArmorItem.Type.LEGGINGS);
        }
    }

    public static class Boots extends MiracleArmorItem {
        public Boots() {
            super(ArmorItem.Type.BOOTS);
        }
    }
}
