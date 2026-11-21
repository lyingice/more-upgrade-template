package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

/** 深渊生物命中时施加坠渊诅咒。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class AbyssMobEvents {

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) {
            return;
        }
        AbyssSkeletonEntity skeleton = null;
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof AbyssSkeletonEntity s) {
            skeleton = s;
        } else if (event.getSource().getDirectEntity() instanceof Arrow arrow
                && arrow.getOwner() instanceof AbyssSkeletonEntity s2) {
            skeleton = s2;
        }
        if (skeleton == null) {
            return;
        }
        // 坠渊诅咒 III
        target.addEffect(new MobEffectInstance(BeastiaryEffects.abyssCurse(),
                AbyssSkeletonEntity.curseSeconds(target.level()) * 20, 2), skeleton);
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (!(event.getExplosion().getDirectSourceEntity() instanceof AbyssCreeperEntity)) {
            return;
        }
        // 爆炸范围内所有生物吃 10 秒坠渊诅咒 IV
        for (Entity entity : event.getAffectedEntities()) {
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(BeastiaryEffects.abyssCurse(), 200, 3));
            }
        }
    }

    private AbyssMobEvents() {
    }
}
