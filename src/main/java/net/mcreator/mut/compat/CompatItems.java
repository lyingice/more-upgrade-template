package net.mcreator.mut.compat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 不依赖任何可选模组的物品工具方法。
 * <p>
 * 本类只引用原版类型，可以在任意环境安全加载。
 */
public final class CompatItems {

    /**
     * 读取物品默认实例上主手 {@code ADD_VALUE} 类型的攻击伤害修饰符并求和。
     * <p>
     * 本模组有一批剑（dragon、nether_star、steel 等）把真实伤害写在
     * {@code Item.Properties.attributes(...)} 的自定义修饰符里，而
     * {@code Tier.getAttackDamageBonus()} 返回 0。因此不能再用 tier+4 推算伤害，
     * 必须直接读取物品的属性修饰符组件。
     *
     * @return 主手攻击伤害修饰符之和；没有则返回 0
     */
    public static float swordBaseAttackDamage(Item sword) {
        ItemStack stack = sword.getDefaultInstance();
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return 0.0F;
        }

        double damage = 0.0D;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().value() == Attributes.ATTACK_DAMAGE.value()
                    && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE
                    && entry.slot().test(EquipmentSlot.MAINHAND)) {
                damage += entry.modifier().amount();
            }
        }
        return (float) damage;
    }

    private CompatItems() {
    }
}
