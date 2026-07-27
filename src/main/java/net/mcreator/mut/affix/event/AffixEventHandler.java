package net.mcreator.mut.affix.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.effect.*;
import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import javax.annotation.Nullable;
import java.util.*;

public class AffixEventHandler {

    /** 遍历装备执行指定 trigger 的效果，返回累计伤害倍率（先求和再乘） */
    public static float applyDamageEffects(LivingEntity attacker, float baseDamage,
                                           DamageSource source, boolean isProjectile) {
        float totalLevel = 0;
        double perLevel = 0;

        for (var slot : EquipmentSlot.values()) {
            ItemStack stack = attacker.getItemBySlot(slot);
            Affix affix = Affix.fromStack(stack);
            if (affix == null) continue;
            int level = Affix.getLevelFromStack(stack);
            for (AffixEffect effect : AffixJsonLoader.getEffects(affix.getId())) {
                if (effect.getTrigger().equals("attack") || effect.getTrigger().equals("always")) {
                    if (effect instanceof DamageMultiplierEffect dme) {
                        if (dme.isRangedOnly() && !isProjectile) continue;
                        totalLevel += level;
                        perLevel = Math.max(perLevel, dme.getPerLevel());
                    } else if (effect instanceof ConditionalMultiplierEffect cme) {
                        if (cme.isActive(attacker)) {
                            totalLevel += level;
                            perLevel = Math.max(perLevel, cme.getPerLevel());
                        }
                    }
                }
            }
        }
        return totalLevel > 0 ? baseDamage * (1.0F + (float)(totalLevel * perLevel)) : baseDamage;
    }

    /** 遍历装备执行 trigger="attack" 的效果（施加印记——先按印记类型求和再 apply） */
    public static void onAttack(LivingEntity attacker, LivingEntity target) {
        Map<String, Integer> markSumMap = new HashMap<>();
        Map<String, int[]> markMeta = new HashMap<>(); // [duration, isMarkId]

        for (var slot : EquipmentSlot.values()) {
            ItemStack stack = attacker.getItemBySlot(slot);
            Affix affix = Affix.fromStack(stack);
            if (affix == null) continue;
            int level = Affix.getLevelFromStack(stack);
            for (AffixEffect effect : AffixJsonLoader.getEffects(affix.getId())) {
                if (effect.getTrigger().equals("attack") && effect instanceof MarkOnAttackEffect me) {
                    String key = me.getEffectKey();
                    markSumMap.merge(key, level, Integer::sum);
                    markMeta.putIfAbsent(key, new int[]{me.getDurationTicks()});
                }
            }
        }

        // 统一施加
        for (var entry : markSumMap.entrySet()) {
            String key = entry.getKey();
            int totalLevel = entry.getValue();
            String effectId = MarkOnAttackEffect.parseEffectId(key);
            int duration = markMeta.getOrDefault(key, new int[]{600})[0];
            applyMark(target, effectId, totalLevel, duration);
        }
    }

    private static void applyMark(LivingEntity target, String effectId, int level, int duration) {
        var holder = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                .getHolder(ResourceLocation.parse(effectId)).orElse(null);
        if (holder == null) return;

        MobEffectInstance existing = target.getEffect(holder);
        int newAmp = level - 1;
        if (existing != null) newAmp = Math.max(newAmp, existing.getAmplifier());
        target.addEffect(new MobEffectInstance(holder, duration, newAmp, false, true, true));
    }

    /** 治疗加成（先求和再算） */
    public static float applyHealEffects(LivingEntity entity, float amount) {
        float totalLevel = 0;
        double perLevel = 0;
        String condition = null;

        for (var slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            Affix affix = Affix.fromStack(stack);
            if (affix == null) continue;
            int level = Affix.getLevelFromStack(stack);
            for (AffixEffect effect : AffixJsonLoader.getEffects(affix.getId())) {
                if (effect instanceof HealBonusEffect hbe) {
                    if (hbe.getCondition() == null || hbe.isConditionMet(entity)) {
                        totalLevel += level;
                        perLevel = Math.max(perLevel, hbe.getPerLevel());
                    }
                }
            }
        }

        return amount + (float)(totalLevel * perLevel);
    }

    /** 属性修改器（Nirvana 等） */
    public static ItemAttributeModifiers applyAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Affix affix = Affix.fromStack(stack);
        if (affix == null) return null;
        ItemAttributeModifiers base = stack.getItem().getDefaultAttributeModifiers();
        for (AffixEffect effect : AffixJsonLoader.getEffects(affix.getId())) {
            if (effect instanceof AttributeModifierEffect ame)
                base = ame.modify(base, slot, affix.getId());
        }
        return base;
    }

    /** 获取所有带 durability_repair 类型效果的装备槽 */
    public static List<EquipmentSlot> getDurabilityRepairSlots(LivingEntity entity) {
        List<EquipmentSlot> slots = new ArrayList<>();
        for (var slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            Affix affix = Affix.fromStack(stack);
            if (affix == null) continue;
            for (AffixEffect e : AffixJsonLoader.getEffects(affix.getId()))
                if (e instanceof DurabilityRepairEffect) { slots.add(slot); break; }
        }
        return slots;
    }

    public static float applyMarkAmplify(LivingEntity target, DamageSource source) {
        float bonus = 0;
        for (var entry : AffixJsonLoader.getMarkAmplifyEntries()) {
            if (entry.markEffect() == null) continue;
            MobEffectInstance mark = target.getEffect(entry.markEffect());
            if (mark == null) continue;

            for (String dtId : entry.damageTypes()) {
                ResourceKey<DamageType> key = ResourceKey.create(
                        Registries.DAMAGE_TYPE, ResourceLocation.parse(dtId));
                if (source.typeHolder().is(key)) {
                    bonus += (mark.getAmplifier() + 1)
                            * (float) AffixConfig.getCoefficient(entry.affixId(), entry.coefficient());
                    break;
                }
            }
        }
        return bonus;
    }
}