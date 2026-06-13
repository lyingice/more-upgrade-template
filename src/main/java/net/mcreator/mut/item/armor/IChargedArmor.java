package net.mcreator.mut.item.armor;

import net.minecraft.resources.ResourceLocation;

/**
 * 实现此接口的盔甲会在装备时渲染闪电苦力怕同款动态能量层
 */
public interface IChargedArmor {
    /**
     * @return 能量层的纹理路径
     */
    ResourceLocation getEnergyTexture();
}