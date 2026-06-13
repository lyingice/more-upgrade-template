package net.mcreator.mut.item.armor;

import net.minecraft.resources.ResourceLocation;

/**
 * 实现此接口的物品在手持时会渲染闪电动态能量层（类似附魔光效）
 */
public interface IChargedItem {
    ResourceLocation getEnergyTexture();
}