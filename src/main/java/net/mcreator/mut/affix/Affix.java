package net.mcreator.mut.affix;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

public interface Affix {

    // ========== 稀有度（等级决定颜色） ==========

    /**
     * 根据等级获取颜色
     * 默认：1-灰 2-绿 3-蓝 4-紫 5-金 6-红
     * 子类可覆写自定义规则
     */
    default ChatFormatting getColorForLevel(int level) {
        // 优先从 JSON 数据包读取颜色
        net.mcreator.mut.affix.data.LevelConfig lc =
                net.mcreator.mut.affix.data.AffixDataLoader.getLevel(level);
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
        // 回退到旧硬编码
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

    /** 全身装备总等级上限（默认 99） */
    default int getTotalMaxLevel() {
        return 99;
    }

    /** 单物品词缀等级上限（动态：从 JSON 配置读取，默认 8） */
    default int getMaxLevel() {
        // 优先从数据驱动配置中读取最大等级
        int jsonMaxLevel = net.mcreator.mut.affix.data.AffixDataLoader.getMaxLevel();
        return jsonMaxLevel > 0 ? jsonMaxLevel : 8;
    }

    // ========== 额外稀有度接口（非数字，自定义字符串 key） ==========

    /**
     * 是否使用自定义稀有度（覆写此方法返回 true 则忽略等级颜色，使用自定义稀有度）
     */
    default boolean useCustomRarity() {
        return false;
    }

    /**
     * 自定义稀有度标识符（如 "ancient", "celestial"）
     * 用于在语言文件中查找颜色和显示名
     */
    @Nullable
    default String getCustomRarityKey() {
        return null;
    }

    /**
     * 根据自定义稀有度 key 获取颜色
     * 默认从语言文件或配置读取，这里提供基础映射
     */
    default ChatFormatting getCustomRarityColor(String key) {
        return switch (key) {
            case "ancient" -> ChatFormatting.DARK_RED;
            case "celestial" -> ChatFormatting.LIGHT_PURPLE;
            case "corrupted" -> ChatFormatting.DARK_PURPLE;
            default -> ChatFormatting.GOLD;
        };
    }

    // ========== 基础信息 ==========

/**
 * 获取ID的方法
 *
 * @return 返回一个字符串类型的ID
 */
    String getId();

    default String getNbtKey() {
        return "Affix";
    }

    // ========== 本地化 ==========

/**
 * 获取修饰词名称的翻译键
 * 该方法返回一个用于本地化修饰词名称的键值
 *
 * @return 返回一个字符串，格式为"affix.{id}.name"，其中{id}是修饰词的唯一标识符
 */
    default String getNameTranslationKey() {
    // 通过拼接"affix."、修饰词ID和".name"来构建翻译键
        return "affix." + getId() + ".name";
    }

    default String getDescriptionTranslationKey() {
        return "affix." + getId() + ".description";
    }

/**
 * 获取显示名称的方法
 * 返回一个可变的组件，该组件使用本地化键来获取对应的显示名称
 *
 * @return 返回一个可变的Component对象，该对象通过本地化键获取显示名称
 */
    default MutableComponent getDisplayName() {
    // 使用Component.translatable方法将翻译键转换为可显示的文本组件
        return Component.translatable(getNameTranslationKey());
    }

/**
 * 获取组件的描述信息
 * 该方法使用翻译键来创建可翻译的文本组件
 *
 * @return 返回一个可翻译的文本组件，使用getDescriptionTranslationKey()作为翻译键
 */
    default MutableComponent getDescription() {
    // 使用Component.translatable()方法创建一个可翻译的组件
    // 传入getDescriptionTranslationKey()获取的翻译键
        return Component.translatable(getDescriptionTranslationKey());
    }

    // ========== NBT 读写 ==========

    /**
     * 写入词缀 + 等级 + 可选自定义稀有度
     */
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

    /** 便捷方法：只用等级，无自定义稀有度 */
    default ItemStack applyToStack(ItemStack stack, int level) {
        return applyToStack(stack, level, null);
    }

    /** 便捷方法：默认 1 级 */
    default ItemStack applyToStack(ItemStack stack) {
        return applyToStack(stack, 1, null);
    }

    @Nullable
    static Affix fromStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("Affix")) {
            String affixId = customData.copyTag().getString("Affix");
            if (!affixId.isEmpty()) {
                return AffixRegistry.get(affixId);
            }
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

    /**
     * 综合获取词缀颜色（优先自定义稀有度，其次等级）
     */
    default ChatFormatting getColor(ItemStack stack) {
        String customKey = Affix.getCustomRarityFromStack(stack);
        if (customKey != null && useCustomRarity()) {
            return getCustomRarityColor(customKey);
        }
        int level = Affix.getLevelFromStack(stack);
        return getColorForLevel(level);
    }
}