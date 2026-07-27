package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.PoisonMarkAffix;
import net.mcreator.mut.affix.impl.FireMarkAffix;
import net.mcreator.mut.affix.impl.WitherMarkAffix;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MarkAffixHelper {

    public static void applyMarkOnAttack(LivingEntity attacker, LivingEntity target, IMarkAffix mark) {
        int equippedLevel = mark.getMarkLevel(attacker);
        if (equippedLevel <= 0) return;

        MobEffectInstance existing = target.getEffect(mark.getMarkEffect());
        int newLevel = equippedLevel;
        if (existing != null) {
            newLevel = Math.max(equippedLevel, existing.getAmplifier() + 1);
        }

        target.addEffect(new MobEffectInstance(
                mark.getMarkEffect(), mark.getMarkDurationTicks(),
                newLevel - 1, false, true, true));

        if (attacker instanceof Player player) {
            player.displayClientMessage(
                    Component.literal("✦ 施加" + mark.getDisplayName().getString()
                            + " " + getLevelRoman(newLevel) + " ✦")
                            .withStyle(mark instanceof PoisonMarkAffix ? ChatFormatting.DARK_GREEN
                                    : mark instanceof FireMarkAffix ? ChatFormatting.GOLD
                                    : mark instanceof WitherMarkAffix ? ChatFormatting.DARK_PURPLE
                                    : ChatFormatting.GRAY),
                    true);
        }
    }

    private static String getLevelRoman(int level) {
        return switch (level) {
            case 1 -> "Ⅰ"; case 2 -> "Ⅱ"; case 3 -> "Ⅲ";
            case 4 -> "Ⅳ"; case 5 -> "Ⅴ"; case 6 -> "Ⅵ";
            default -> "";
        };
    }
}
