package net.mcreator.mut.affix;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

/**
 * 印记类词条统一接口
 * 所有"攻击时给目标附加状态效果"的词条实现此接口
 */
public interface IMarkAffix extends Affix {

    /** 印记持续时间（tick） */
    int getMarkDurationTicks();

    /** 获取印记对应的药水效果 */
    Holder<MobEffect> getMarkEffect();

    /** 计算攻击者身上的印记等级 */
    int getMarkLevel(LivingEntity attacker);
}