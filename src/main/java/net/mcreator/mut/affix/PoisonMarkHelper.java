package net.mcreator.mut.affix;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.mcreator.mut.affix.impl.PoisonMarkAffix;

/**
 * 剧毒印记等级计算器
 * 统计玩家身上所有带有"poison_mark"词条的装备数量
 */
public class PoisonMarkHelper {

    // 最大印记等级（全身4件盔甲 + 主手 + 副手 = 6）
    public static final int MAX_LEVEL = 6;

    /**
     * 计算玩家身上的剧毒印记等级
     * @param entity 实体
     * @return 印记等级 (1-6)
     */
    public static int getPoisonMarkLevel(LivingEntity entity) {
        int count = 0;

        // 检查主手
        if (hasPoisonMark(entity.getMainHandItem())) count++;

        // 检查副手
        if (hasPoisonMark(entity.getOffhandItem())) count++;

        // 检查盔甲槽位
        if (hasPoisonMark(entity.getItemBySlot(EquipmentSlot.HEAD))) count++;
        if (hasPoisonMark(entity.getItemBySlot(EquipmentSlot.CHEST))) count++;
        if (hasPoisonMark(entity.getItemBySlot(EquipmentSlot.LEGS))) count++;
        if (hasPoisonMark(entity.getItemBySlot(EquipmentSlot.FEET))) count++;

        return Math.min(count, MAX_LEVEL);
    }

    /**
     * 检查物品是否带有剧毒印记词条（使用 Affix 接口方法）
     */
    private static boolean hasPoisonMark(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Affix affix = Affix.fromStack(stack);
        return affix instanceof PoisonMarkAffix;
    }

    /**
     * 获取印记等级的显示名称
     */
    public static String getLevelName(int level) {
        return switch (level) {
            case 1 -> "Ⅰ";
            case 2 -> "Ⅱ";
            case 3 -> "Ⅲ";
            case 4 -> "Ⅳ";
            case 5 -> "Ⅴ";
            case 6 -> "Ⅵ";
            default -> "";
        };
    }

    /**
     * 获取印记等级的伤害加成
     */
    public static int getDamageBonus(int level) {
        return level; // 每级增加1点中毒伤害
    }
}