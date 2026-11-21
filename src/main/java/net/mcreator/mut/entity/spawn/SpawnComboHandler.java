package net.mcreator.mut.entity.spawn;

import net.mcreator.mut.MutMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import java.util.List;

/**
 * 生成组合的执行端：主生物落位前，按配置给它安排骑乘者。
 * <p>
 * 两条入口：
 * <ul>
 *   <li>{@link #applyCombos} —— 自己写生成逻辑的地方（例：旅行者幻翼生成器）直接调用；</li>
 *   <li>{@link #onFinalizeSpawn} —— 游戏自身的自然生成路径会触发 FinalizeSpawnEvent，走这里。</li>
 * </ul>
 * 注意：直接调用 {@code mob.finalizeSpawn(...)} 并不会触发该事件，所以自建生成器必须手动调 applyCombos。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class SpawnComboHandler {

    /** 防止骑乘者自身再触发一次组合判定造成递归 */
    private static boolean resolving = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.isSpawnCancelled()) return;
        Mob host = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        applyCombos(host, level, event.getSpawnType());
    }

    /**
     * 按配置给主生物安排骑乘者。骑乘者不单独加入世界，
     * 挂到主生物身上后由主生物那次 addFreshEntityWithPassengers 一起带入。
     */
    public static void applyCombos(Mob host, ServerLevel level, MobSpawnType spawnType) {
        if (resolving) return;
        List<SpawnCombo> combos = SpawnComboRegistry.forHost(host.getType());
        if (combos.isEmpty()) return;

        resolving = true;
        try {
            for (SpawnCombo combo : combos) {
                if (!combo.appliesTo(spawnType)) continue;
                if (combo.riderType() == host.getType()) continue;
                if (level.random.nextFloat() >= combo.chance()) continue;

                int amount = combo.minCount()
                        + (combo.maxCount() > combo.minCount()
                        ? level.random.nextInt(combo.maxCount() - combo.minCount() + 1) : 0);

                for (int i = 0; i < amount; i++) {
                    if (i > 0 && !host.getPassengers().isEmpty()) break;
                    Entity rider = combo.riderType().create(level);
                    if (rider == null) continue;
                    rider.moveTo(host.getX(), host.getY(), host.getZ(),
                            level.random.nextFloat() * 360.0F, 0.0F);
                    if (!rider.startRiding(host, true)) {
                        rider.discard();
                        MutMod.LOGGER.debug("[spawn-combo] {} 无法骑乘 {}", combo.rider(), combo.host());
                    }
                }
            }
        } finally {
            resolving = false;
        }
    }
}
