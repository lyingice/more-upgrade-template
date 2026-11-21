package net.mcreator.mut.porting.armorstand.compat.jade;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.armorstand.MutArmorStandItems;
import net.mcreator.mut.porting.armorstand.WolfArmorStandEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

/** Jade：显示狼铠展示架上穿着的狼铠。 */
public enum MutWolfArmorStandProvider implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "wolf_armor_stand_armor");
    }

    @Override
    public int getDefaultPriority() {
        return 1500;
    }

    @Override
    public IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
        return IElementHelper.get().item(new ItemStack(MutArmorStandItems.WOLF_ARMOR_STAND.get()));
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof WolfArmorStandEntity stand)) {
            return;
        }

        ItemStack armor = stand.getBodyArmorItem();
        if (armor.isEmpty()) {
            return;
        }

        tooltip.add(IElementHelper.get().smallItem(armor));
        tooltip.append(armor.getHoverName());
    }
}
