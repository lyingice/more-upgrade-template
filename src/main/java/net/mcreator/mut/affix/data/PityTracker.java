package net.mcreator.mut.affix.data;

import net.mcreator.mut.MutMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

public class PityTracker {

    private static final String PITY_TAG_KEY = "AffixPity";

    public static int getPity(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd != null && cd.contains(PITY_TAG_KEY)) return Math.max(0, cd.copyTag().getInt(PITY_TAG_KEY));
        return 0;
    }

    public static void incrementPity(ItemStack stack) {
        if (stack.isEmpty()) return;
        PityConfig config = AffixDataLoader.getPityConfig();
        if (!config.isEnabled()) return;

        int current = getPity(stack);
        int perAttempt = getPityPerAttempt(stack, config);
        int newPity = Math.min(current + perAttempt, config.getGlobal().getPityCap());
        setPity(stack, newPity);
    }

    public static void resetPity(ItemStack stack) { if (!stack.isEmpty()) setPity(stack, 0); }

    public static float getPityBonusMultiplier(ItemStack stack) {
        PityConfig config = AffixDataLoader.getPityConfig();
        if (!config.isEnabled()) return 1.0F;
        int pity = getPity(stack);
        if (pity <= 0) return 1.0F;

        double bonusPerPoint = config.getGlobal().getPityBonusPerPoint();
        // ★ 修复：检查材料特定覆盖
        if (config.getPerMaterialOverrides() != null && !stack.isEmpty()) {
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            PityConfig.PerMaterialOverride override = config.getPerMaterialOverrides().get(itemId);
            if (override != null && override.getPityBonusPerPoint() != null)
                bonusPerPoint = override.getPityBonusPerPoint();
        }
        return 1.0F + (float)(pity * bonusPerPoint);
    }

    public static boolean shouldReset(int rolledLevel, ItemStack stack) {
        PityConfig config = AffixDataLoader.getPityConfig();
        if (!config.isEnabled() || !config.getGlobal().isPityResetOnSuccess()) return false;

        int minLevel = config.getGlobal().getMinLevelForReset();
        if (config.getPerMaterialOverrides() != null && !stack.isEmpty()) {
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            PityConfig.PerMaterialOverride override = config.getPerMaterialOverrides().get(itemId);
            if (override != null && override.getMinLevelForReset() != null)
                minLevel = override.getMinLevelForReset();
        }
        return rolledLevel >= minLevel;
    }

    private static int getPityPerAttempt(ItemStack stack, PityConfig config) {
        int base = config.getGlobal().getPityPerAttempt();
        if (config.getPerMaterialOverrides() != null && !stack.isEmpty()) {
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            PityConfig.PerMaterialOverride override = config.getPerMaterialOverrides().get(itemId);
            if (override != null && override.getPityPerAttempt() != null)
                base = override.getPityPerAttempt();
        }
        return base;
    }

    private static void setPity(ItemStack stack, int value) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = (cd != null) ? cd.copyTag() : new CompoundTag();
        tag.putInt(PITY_TAG_KEY, Math.max(0, value));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
