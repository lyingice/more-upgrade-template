package net.mcreator.mut.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorMaterial;

/**
 * 盔甲材质 Holder 集中管理
 * <p>
 * 供生成的盔甲类（如 AmethystArmor）引用，
 * 避免依赖 MutModItems。
 * <p>
 * 每新增一种带盔甲的材质，在这里加一个静态字段。
 */
public class MutArmor {

    public static Holder<ArmorMaterial> AMETHYST_ARMOR_MATERIAL;

    /**
     * 在游戏注册完成后调用，初始化所有 Holder。
     */
    public static void init() {
        AMETHYST_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(
                MutMaterials.AMETHYST.asArmorMaterial()
        );
    }
}