package net.mcreator.mut.affix;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.mcreator.mut.affix.impl.PoisonMarkAffix;
import net.mcreator.mut.affix.impl.FireMarkAffix;
import net.mcreator.mut.affix.impl.WitherMarkAffix;
/**
 * 印记词条通用处理器
 * 所有"攻击时附加状态效果"类词条统一调用此方法
 */
public class MarkAffixHelper {

    /**
     * 处理攻击附加印记（近战+远程通用）
     *
     * @param attacker 攻击者
     * @param target   被攻击目标
     * @param mark     印记词条（实现 IMarkAffix 接口）
     */
    public static void applyMarkOnAttack(LivingEntity attacker, LivingEntity target, IMarkAffix mark) {
        // 计算印记等级
        int markLevel = mark.getMarkLevel(attacker);
        if (markLevel <= 0) return;

        // 获取目标已有印记
        MobEffectInstance existing = target.getEffect(mark.getMarkEffect());
        int finalLevel = markLevel;
        if (existing != null) {
            int existingLevel = existing.getAmplifier() + 1;
            finalLevel = Math.max(markLevel, existingLevel);
        }

        // 施加新印记
        target.addEffect(new MobEffectInstance(
                mark.getMarkEffect(),
                mark.getMarkDurationTicks(),
                finalLevel - 1,
                false, true, true
        ));

        // 给玩家提示（未来可扩展为配置开关）
        if (attacker instanceof Player player) {
            player.displayClientMessage(
                    Component.literal("✦ 施加" +
                                    mark.getDisplayName().getString() + " " +
                                    getLevelRoman(finalLevel) + " ✦")
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