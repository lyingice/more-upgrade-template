package net.mcreator.mut.affix;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
@Deprecated
public interface IMarkAffix extends Affix {

    int getMarkDurationTicks();

    Holder<MobEffect> getMarkEffect();

    int getMarkLevel(LivingEntity attacker);
}
