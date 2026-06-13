package net.mcreator.mut.affix.data;

import net.mcreator.mut.MutMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

/**
 * 软保底追踪器 - 管理物品 NBT 中的 pity 累计值
 * 存储在 "AffixPity" 键下
 */
public class PityTracker {

    private static final String PITY_TAG_KEY = "AffixPity";

    /**
     * 获取物品当前的 pity 值
     */
    public static int getPity(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(PITY_TAG_KEY)) {
            return Math.max(0, customData.copyTag().getInt(PITY_TAG_KEY));
        }
        return 0;
    }

    /**
     * 增加 pity 值（上限由配置决定）
     */
    public static void incrementPity(ItemStack stack) {
        if (stack.isEmpty()) return;

        PityConfig config = AffixDataLoader.getPityConfig();
        if (!config.isEnabled()) return;

        int currentPity = getPity(stack);
        int pityPerAttempt = getPityPerAttempt(stack, config);

        int newPity = Math.min(currentPity + pityPerAttempt, config.getGlobal().getPityCap());
        setPity(stack, newPity);
    }

    /**
     * 重置 pity 值为 0
     */
    public static void resetPity(ItemStack stack) {
        if (stack.isEmpty()) return;
        setPity(stack, 0);
    }

    /**
     * 获取当前 pity 带来的等级权重加成倍率
     */
    public static float getPityBonusMultiplier(ItemStack stack) {
        PityConfig config = AffixDataLoader.getPityConfig();
        if (!config.isEnabled()) return 1.0F;

        int pity = getPity(stack);
        if (pity <= 0) return 1.0F;

        double bonusPerPoint = config.getGlobal().getPityBonusPerPoint();

        // 检查是否有材料特定的覆盖
        if (config.getPerMaterialOverrides() != null) {
            // 这里简化处理，使用全局值
        }

        return 1.0F + (float)(pity * bonusPerPoint);
    }

    /**
     * 判断是否应该"重置"pity（出高等级时）
     */
    public static boolean shouldReset(int rolledLevel, ItemStack stack) {
        PityConfig config = AffixDataLoader.getPityConfig();
        if (!config.isEnabled()) return false;
        if (!config.getGlobal().isPityResetOnSuccess()) return false;

        int minLevel = config.getGlobal().getMinLevelForReset();
        return rolledLevel >= minLevel;
    }

    /**
     * 根据材料和配置获取每次 pity 积累值
     */
    private static int getPityPerAttempt(ItemStack stack, PityConfig config) {
        return config.getGlobal().getPityPerAttempt();
    }

    /**
     * 写入 pity 到物品 NBT
     */
    private static void setPity(ItemStack stack, int value) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = (customData != null) ? customData.copyTag() : new CompoundTag();
        tag.putInt(PITY_TAG_KEY, Math.max(0, value));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
