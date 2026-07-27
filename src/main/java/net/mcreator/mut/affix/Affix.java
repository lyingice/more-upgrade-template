package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.AffixDataLoader;
import net.mcreator.mut.affix.data.LevelConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

public interface Affix {

    // ========== 稀有度（等级决定颜色） ==========

    default ChatFormatting getColorForLevel(int level) {
        LevelConfig lc = AffixDataLoader.getLevel(level);
        if (lc != null && lc.getFormatting() != null) {
            String fmt = lc.getFormatting().toLowerCase();
            return switch (fmt) {
                case "white" -> ChatFormatting.WHITE;
                case "blue" -> ChatFormatting.BLUE;
                case "gold" -> ChatFormatting.GOLD;
                case "light_purple" -> ChatFormatting.LIGHT_PURPLE;
                case "yellow" -> ChatFormatting.YELLOW;
                case "red" -> ChatFormatting.RED;
                case "dark_red" -> ChatFormatting.DARK_RED;
                case "dark_purple" -> ChatFormatting.DARK_PURPLE;
                case "gray" -> ChatFormatting.GRAY;
                case "green" -> ChatFormatting.GREEN;
                case "dark_green" -> ChatFormatting.DARK_GREEN;
                case "aqua" -> ChatFormatting.AQUA;
                case "dark_aqua" -> ChatFormatting.DARK_AQUA;
                default -> ChatFormatting.WHITE;
            };
        }
        return switch (level) {
            case 1 -> ChatFormatting.WHITE;
            case 2 -> ChatFormatting.BLUE;
            case 3 -> ChatFormatting.GOLD;
            case 4 -> ChatFormatting.LIGHT_PURPLE;
            case 5 -> ChatFormatting.YELLOW;
            case 6 -> ChatFormatting.RED;
            default -> level > 6 ? ChatFormatting.DARK_RED : ChatFormatting.GRAY;
        };
    }

    default int getTotalMaxLevel() { return 99; }

    default int getMaxLevel() {
        int jsonMaxLevel = AffixDataLoader.getMaxLevel();
        return jsonMaxLevel > 0 ? jsonMaxLevel : 8;
    }

    // ========== 自定义稀有度 ==========

    default boolean useCustomRarity() { return false; }

    @Nullable
    default String getCustomRarityKey() { return null; }

    default ChatFormatting getCustomRarityColor(String key) {
        return switch (key) {
            case "ancient" -> ChatFormatting.DARK_RED;
            case "celestial" -> ChatFormatting.LIGHT_PURPLE;
            case "corrupted" -> ChatFormatting.DARK_PURPLE;
            default -> ChatFormatting.GOLD;
        };
    }

    // ========== 基础信息 ==========

    String getId();

    default String getNbtKey() { return "Affix"; }

    // ========== 本地化 ==========

    default String getNameTranslationKey() { return "affix." + getId() + ".name"; }

    default String getDescriptionTranslationKey() { return "affix." + getId() + ".description"; }

    default MutableComponent getDisplayName() { return Component.translatable(getNameTranslationKey()); }

    default MutableComponent getDescription() { return Component.translatable(getDescriptionTranslationKey()); }

    // ========== NBT 读写 ==========

    default ItemStack applyToStack(ItemStack stack, int level, @Nullable String customRarityKey) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = (customData != null) ? customData.copyTag() : new CompoundTag();
        tag.putString(getNbtKey(), getId());
        tag.putInt("AffixLevel", Math.clamp(level, 1, getMaxLevel()));
        if (customRarityKey != null && !customRarityKey.isEmpty()) {
            tag.putString("AffixCustomRarity", customRarityKey);
        } else {
            tag.remove("AffixCustomRarity");
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    default ItemStack applyToStack(ItemStack stack, int level) { return applyToStack(stack, level, null); }

    default ItemStack applyToStack(ItemStack stack) { return applyToStack(stack, 1, null); }

    @Nullable
    static Affix fromStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("Affix")) {
            String affixId = customData.copyTag().getString("Affix");
            if (!affixId.isEmpty()) return AffixRegistry.get(affixId);
        }
        return null;
    }

    static int getLevelFromStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("AffixLevel")) {
            return Math.max(1, customData.copyTag().getInt("AffixLevel"));
        }
        return 1;
    }

    @Nullable
    static String getCustomRarityFromStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("AffixCustomRarity")) {
            return customData.copyTag().getString("AffixCustomRarity");
        }
        return null;
    }

    default ChatFormatting getColor(ItemStack stack) {
        String customKey = Affix.getCustomRarityFromStack(stack);
        if (customKey != null && useCustomRarity()) return getCustomRarityColor(customKey);
        int level = Affix.getLevelFromStack(stack);
        return getColorForLevel(level);
    }

    // ========== ★ 泛化装备等级计算（消除11处重复） ==========

    default int getEquippedLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        return Math.min(total, getTotalMaxLevel());
    }

    private int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix != null && affix.getId().equals(getId())) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    // ========== ★ 属性修改器（Nirvana 复活 + 未来扩展） ==========

    default boolean hasAttributeModifiers() { return false; }

    @Nullable
    default ItemAttributeModifiers getAttributeModifiers(ItemAttributeModifiers original, EquipmentSlot slot) {
        return original;
    }
}
