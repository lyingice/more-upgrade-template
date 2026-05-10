package net.mcreator.mut.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.PoisonMarkHelper;
import net.mcreator.mut.affix.FireMarkHelper;
import net.mcreator.mut.affix.WitherMarkHelper;
import net.mcreator.mut.affix.impl.PoisonMarkAffix;
import net.mcreator.mut.affix.impl.FireMarkAffix;
import net.mcreator.mut.affix.impl.WitherMarkAffix;
import net.mcreator.mut.potion.PoisonMarkMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public class AffixEventHandler {

    @SubscribeEvent
    public void onAttack(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity attacker)) return;

        LivingEntity target = event.getEntity();
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty()) return;

        Affix affix = Affix.fromStack(weapon);
        if (affix == null) return;

        // ========== 凋零印记 ==========
        if (affix instanceof WitherMarkAffix) {
            int markLevel = WitherMarkHelper.getWitherMarkLevel(attacker);
            if (markLevel <= 0) return;

            MobEffectInstance existing = target.getEffect(getEffect("wither_mark"));
            int finalLevel = markLevel;
            if (existing != null) {
                int existingLevel = existing.getAmplifier() + 1;
                finalLevel = Math.max(markLevel, existingLevel);
            }

            target.addEffect(new MobEffectInstance(
                    getEffect("wither_mark"),
                    WitherMarkAffix.MARK_DURATION_TICKS,
                    finalLevel - 1,
                    false, true, true
            ));
        }
        // ========== 剧毒印记 ==========
        if (affix instanceof PoisonMarkAffix) {
            int markLevel = PoisonMarkHelper.getPoisonMarkLevel(attacker);
            if (markLevel <= 0) return;

            MobEffectInstance existing = target.getEffect(getEffect("poison_mark"));
            int finalLevel = markLevel;
            if (existing != null) {
                int existingLevel = PoisonMarkMobEffect.getMarkLevel(existing);
                finalLevel = Math.max(markLevel, existingLevel);
            }

            target.addEffect(new MobEffectInstance(
                    getEffect("poison_mark"),
                    PoisonMarkAffix.MARK_DURATION_TICKS,
                    finalLevel - 1,
                    false, true, true
            ));
        }

        // ========== 灼烧印记 ==========
        if (affix instanceof FireMarkAffix) {
            int markLevel = FireMarkHelper.getFireMarkLevel(attacker);
            if (markLevel <= 0) return;

            MobEffectInstance existing = target.getEffect(getEffect("fire_mark"));
            int finalLevel = markLevel;
            if (existing != null) {
                int existingLevel = existing.getAmplifier() + 1;
                finalLevel = Math.max(markLevel, existingLevel);
            }

            target.addEffect(new MobEffectInstance(
                    getEffect("fire_mark"),
                    FireMarkAffix.MARK_DURATION_TICKS,
                    finalLevel - 1,
                    false, true, true
            ));
        }
    }

    private Holder<MobEffect> getEffect(String name) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(
                ResourceLocation.fromNamespaceAndPath("mut", name)
        ).orElse(null);
    }
}