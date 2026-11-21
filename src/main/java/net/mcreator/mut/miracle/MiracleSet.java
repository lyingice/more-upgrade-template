package net.mcreator.mut.miracle;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * 奇迹套装的查询入口 —— 给最终 BOSS（模板之神）、套装效果和别的系统共用。
 * <p>
 * 设计上这套装备"是否穿齐"是判断很多机制的开关，所以判定逻辑只放在这一处，
 * 不要在别处各自遍历装备槽。
 */
public final class MiracleSet {

    /** 四件护甲统一打这个标签，BOSS 和效果系统都从标签读，不写死物品 id */
    public static final TagKey<Item> ARMOR_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("mut", "miracle_armor"));

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /** 穿了几件（0~4） */
    public static int wornPieces(LivingEntity entity) {
        int worn = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (entity.getItemBySlot(slot).is(ARMOR_TAG)) {
                worn++;
            }
        }
        return worn;
    }

    /** 是否穿齐一整套 */
    public static boolean isWearingFullSet(LivingEntity entity) {
        return wornPieces(entity) >= ARMOR_SLOTS.length;
    }

    /** 手上是不是奇迹之剑 */
    public static boolean isHoldingMiracleSword(LivingEntity entity) {
        return entity.getMainHandItem().is(MiracleItems.MIRACLE_SWORD.get());
    }

    /**
     * "奇迹武装"：整套奇迹护甲 + 奇迹之剑。
     * <p>
     * 最终 BOSS 的"模板篡改"应当对满足这个条件的实体失效 ——
     * 这是"没有这套装备就打不过"的唯一判定口，别在 BOSS 里另写一套。
     */
    public static boolean isMiracleArmed(LivingEntity entity) {
        return isWearingFullSet(entity) && isHoldingMiracleSword(entity);
    }

    private MiracleSet() {
    }
}
