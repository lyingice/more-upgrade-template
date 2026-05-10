package net.mcreator.mut.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PoisonMarkMobEffect extends MobEffect {

    public PoisonMarkMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x44AA44);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每 20 tick 显示一次粒子
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 在实体周围生成绿色粒子
        if (entity.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                entity.level().addParticle(
                        (ParticleOptions) ParticleTypes.ENTITY_EFFECT,
                        entity.getRandomX(0.6),
                        entity.getRandomY(),
                        entity.getRandomZ(0.6),
                        0.0, 0.6, 0.0  // RGB: 深绿色
                );
            }
        }
        return true;
    }

    @Override
    public void fillEffectCures(Set<net.neoforged.neoforge.common.EffectCure> cures, @NotNull MobEffectInstance effectInstance) {
        cures.add(net.neoforged.neoforge.common.EffectCures.MILK);
        cures.add(net.neoforged.neoforge.common.EffectCures.PROTECTED_BY_TOTEM);
    }

    /**
     * 获取印记等级
     */
    public static int getMarkLevel(MobEffectInstance instance) {
        if (instance == null) return 0;
        return instance.getAmplifier() + 1;
    }

    /**
     * 创建带等级的印记效果
     */
    public static MobEffectInstance create(int level, int durationTicks) {
        Holder<MobEffect> effectHolder = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getHolder(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("mut", "poison_mark")
        ).orElseThrow();

        return new MobEffectInstance(
                effectHolder,
                durationTicks,
                level - 1,
                false,   // ambient - false 会显示更明显的粒子
                true,    // visible - true 显示图标
                true     // showIcon
        );
    }
}