package net.mcreator.mut.trait;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class TraitEventHandler {

    @SubscribeEvent
    public void onAttack(LivingIncomingDamageEvent event) {
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) return;
        handleTrigger("attack", attacker, event.getEntity(), null);
    }
    private LivingEntity resolveAttacker(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity direct) return direct;
        if (event.getSource().getEntity() instanceof LivingEntity source) return source;
        return null;
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            handleTrigger("kill", attacker, event.getEntity(), null);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        handleTrigger("block_break", event.getPlayer(), null, event.getState());
    }

    private void handleTrigger(String trigger, LivingEntity user, @Nullable LivingEntity target,
                               @Nullable net.minecraft.world.level.block.state.BlockState broken) {
        ItemStack stack = user.getMainHandItem();
        for (Trait trait : TraitRegistry.getTraitsFor(stack)) {
            for (TraitEffect effect : trait.getEffects()) {
                if (effect.getTrigger().equals(trigger) || effect.getTrigger().equals("always")) {
                    effect.apply(user, target, stack, broken);
                }
            }
        }
    }
}