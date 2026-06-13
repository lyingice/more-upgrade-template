package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.PoisonMarkAffix;
import net.mcreator.mut.affix.impl.FireMarkAffix;
import net.mcreator.mut.affix.impl.WitherMarkAffix;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class MarkAffixHelper {

    public static void applyMarkOnAttack(LivingEntity attacker, LivingEntity target, IMarkAffix mark) {
        // 获取攻击者装备的总等级
        int equippedLevel = mark.getMarkLevel(attacker);
        if (equippedLevel <= 0) return;

        // 获取目标已有印记
        MobEffectInstance existing = target.getEffect(mark.getMarkEffect());
        int newLevel = equippedLevel;
        if (existing != null) {
            int existingLevel = existing.getAmplifier() + 1;
            newLevel = Math.max(equippedLevel, existingLevel);
        }

        // 施加印记（amplifier = 等级 - 1）
        target.addEffect(new MobEffectInstance(
                mark.getMarkEffect(),
                mark.getMarkDurationTicks(),
                newLevel - 1,
                false, true, true
        ));

        if (attacker instanceof Player player) {
            player.displayClientMessage(
                    Component.literal("✦ 施加" + mark.getDisplayName().getString() + " " + getLevelRoman(newLevel) + " ✦")
                            .withStyle(getMarkColor(mark)),
                    true
            );
        }
    }

    private static String getLevelRoman(int level) {
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

    private static ChatFormatting getMarkColor(IMarkAffix mark) {
        if (mark instanceof PoisonMarkAffix) return ChatFormatting.DARK_GREEN;
        if (mark instanceof FireMarkAffix) return ChatFormatting.GOLD;
        if (mark instanceof WitherMarkAffix) return ChatFormatting.DARK_PURPLE;
        return ChatFormatting.GRAY;
    }
}