package net.mcreator.mut.armorset;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 套装效果的触发入口。只在服务端求值，客户端不参与。
 */
public class ArmorSetEventHandler {

    private static final int PIECES_MIN = 1;

    @SubscribeEvent
    public void onHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) return;
        dispatch(player, "hurt");
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || player.level().isClientSide()) return;
        dispatch(player, "kill");
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;
        int seconds = player.tickCount / 20;
        dispatchInterval(player, seconds);
    }

    private void dispatch(Player player, String trigger) {
        for (ArmorSet set : ArmorSetRegistry.getAll()) {
            int worn = set.countWorn(player);
            if (worn < PIECES_MIN) continue;
            for (ArmorSet.Bonus bonus : set.getBonuses()) {
                if (worn < bonus.pieces()) continue;
                for (ArmorSetEffect effect : bonus.effects()) {
                    if (effect.getTrigger().equals(trigger)) {
                        try {
                            effect.apply(player, set);
                        } catch (RuntimeException ex) {
                            net.mcreator.mut.MutMod.LOGGER.warn("Armor set {} effect failed", set.getId(), ex);
                        }
                    }
                }
            }
        }
    }

    private void dispatchInterval(Player player, int seconds) {
        for (ArmorSet set : ArmorSetRegistry.getAll()) {
            int worn = set.countWorn(player);
            if (worn < PIECES_MIN) continue;
            for (ArmorSet.Bonus bonus : set.getBonuses()) {
                if (worn < bonus.pieces()) continue;
                for (ArmorSetEffect effect : bonus.effects()) {
                    if (!effect.getTrigger().equals("interval")) continue;
                    int interval = effect.getIntervalSeconds();
                    if (interval <= 0 || seconds % interval != 0) continue;
                    try {
                        effect.apply(player, set);
                    } catch (RuntimeException ex) {
                        net.mcreator.mut.MutMod.LOGGER.warn("Armor set {} effect failed", set.getId(), ex);
                    }
                }
            }
        }
    }
}
