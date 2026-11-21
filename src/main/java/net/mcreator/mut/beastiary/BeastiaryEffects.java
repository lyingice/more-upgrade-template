package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 新增状态效果注册。 */
public final class BeastiaryEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, MutMod.MODID);

    /** 坠渊诅咒：按等级削减生命上限（III 减 4，IV 减 5）。 */
    public static final DeferredHolder<MobEffect, AbyssCurseEffect> ABYSS_CURSE =
            EFFECTS.register("abyss_curse", AbyssCurseEffect::new);

    public static Holder<MobEffect> abyssCurse() {
        return ABYSS_CURSE.getDelegate();
    }

    private BeastiaryEffects() {
    }
}
