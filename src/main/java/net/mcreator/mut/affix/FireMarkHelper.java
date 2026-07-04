package net.mcreator.mut.affix;

import net.mcreator.mut.config.AffixConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.mcreator.mut.affix.impl.FireMarkAffix;

public class FireMarkHelper {

    public static int getTotalFireMarkLevel(LivingEntity target) {
        MobEffectInstance mark = target.getEffect(
                BuiltInRegistries.MOB_EFFECT.getHolder(
                        ResourceLocation.fromNamespaceAndPath("mut", "fire_mark")
                ).orElse(null)
        );
        if (mark == null) return 0;
        return mark.getAmplifier() + 1;
    }

    public static int getEquippedFireMarkLevel(LivingEntity entity) {
        int total = 0;
        total += getSlotLevel(entity.getMainHandItem());
        total += getSlotLevel(entity.getOffhandItem());
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.HEAD));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.CHEST));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.LEGS));
        total += getSlotLevel(entity.getItemBySlot(EquipmentSlot.FEET));
        int max = AffixRegistry.FIRE_MARK.getTotalMaxLevel();
        return Math.min(total, max);
    }

    private static int getSlotLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Affix affix = Affix.fromStack(stack);
        if (affix instanceof FireMarkAffix) {
            return Affix.getLevelFromStack(stack);
        }
        return 0;
    }

    public static float getDamageBonus(LivingEntity target) {
        return getTotalFireMarkLevel(target) * (float) AffixConfig.getCoefficient("fire_mark");
    }
}