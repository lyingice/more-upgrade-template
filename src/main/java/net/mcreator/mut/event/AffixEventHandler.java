package net.mcreator.mut.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.IMarkAffix;
import net.mcreator.mut.affix.MarkAffixHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
@Deprecated
public class AffixEventHandler {

    @SubscribeEvent
    public void onAttack(LivingIncomingDamageEvent event) {
        // 获取攻击者（近战+远程通用）
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) return;

        LivingEntity target = event.getEntity();
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty()) return;

        // 检查武器词条
        Affix affix = Affix.fromStack(weapon);

        // 如果是印记类词条，统一处理
        if (affix instanceof IMarkAffix mark) {
            MarkAffixHelper.applyMarkOnAttack(attacker, target, mark);
        }
    }

    /**
     * 解析真正的攻击者（兼容近战和远程）
     */
    private LivingEntity resolveAttacker(LivingIncomingDamageEvent event) {
        // 先尝试直接攻击者（近战）
        if (event.getSource().getDirectEntity() instanceof LivingEntity direct) {
            return direct;
        }
        // 再尝试伤害来源实体（箭/三叉戟的所有者）
        if (event.getSource().getEntity() instanceof LivingEntity source) {
            return source;
        }
        return null;
    }
}