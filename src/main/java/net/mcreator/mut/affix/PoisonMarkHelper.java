package net.mcreator.mut.affix;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.mcreator.mut.affix.impl.PoisonMarkAffix;

public class PoisonMarkHelper {

    /**
     * 获取目标身上的剧毒印记效果等级
     */
    public static int getTotalPoisonMarkLevel(LivingEntity target) {
        MobEffectInstance mark = target.getEffect(
                BuiltInRegistries.MOB_EFFECT.getHolder(
                        ResourceLocation.fromNamespaceAndPath("mut", "poison_mark")
                ).orElse(null)
        );
        if (mark == null) return 0;
        return mark.getAmplifier() + 1;
    }

    /**
     * 获取攻击者装备的剧毒印记词缀等级总和
     */
    public static int getEquippedPoisonMarkLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.POISON_MARK.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof PoisonMarkAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    /**
     * 获取印记等级的伤害加成（等级 × 0.5）
     */
    public static float getDamageBonus(LivingEntity target) {
        return getTotalPoisonMarkLevel(target) * 0.5F;
    }

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
}