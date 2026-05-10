package net.mcreator.mut.affix;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import javax.annotation.Nullable;

public interface Affix {

    String getId();

    default String getNbtKey() {
        return "Affix";
    }

    // ========== 本地化 ==========
    default String getNameTranslationKey() {
        return "affix." + getId() + ".name";
    }

    default String getDescriptionTranslationKey() {
        return "affix." + getId() + ".description";
    }

    default MutableComponent getDisplayName() {
        return Component.translatable(getNameTranslationKey());
    }

    default MutableComponent getDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    // ========== 属性加成（留给涅槃等词条覆写） ==========

    /** 是否有属性加成 */
    default boolean hasAttributeModifiers() {
        return false;
    }

    /** 获取词条提供的属性加成 */
    default ItemAttributeModifiers getAttributeModifiers(ItemAttributeModifiers original, EquipmentSlot slot) {
        return original;
    }

    // ========== NBT 读写 ==========
    default ItemStack applyToStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag;

        if (customData != null) {
            tag = customData.copyTag();
        } else {
            tag = new CompoundTag();
        }

        tag.putString(getNbtKey(), getId());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
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

    enum ToolType {
        SWORD, PICKAXE, AXE, SHOVEL, HOE
    }

    enum ArmorSlotType {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }
}