package net.mcreator.mut.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.mcreator.mut.item.DragonElytraItem;

@EventBusSubscriber(modid = "mut", value = Dist.CLIENT)
public class ElytraJumpBoostHandler {

    // ========== 可调参数 ==========
    private static final double THRUST_POWER = 1.5;
    private static final double SMOOTH_FACTOR = 0.5;
    private static final double TURN_SENSITIVITY = 0.1;
    private static final int COOLDOWN_TICKS = 5;

    // 冷却计时器（每个玩家独立）
    private static final java.util.Map<Player, Integer> COOLDOWNS = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // ========== 1. 检查是否在滑翔 ==========
        if (!player.isFallFlying()) {
            COOLDOWNS.remove(player);
            return;
        }

        // ========== 2. 检查是否穿着 DragonElytraItem ==========
        ItemStack chestSlot = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestSlot.getItem() instanceof DragonElytraItem)) {
            COOLDOWNS.remove(player);
            return;
        }

        // 排除创造和旁观模式
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        // ========== 3. 冷却处理 ==========
        int cooldown = COOLDOWNS.getOrDefault(player, 0);
        if (cooldown > 0) {
            COOLDOWNS.put(player, cooldown - 1);
            return;
        }

        // ========== 4. 检测跳跃键 ==========
        boolean isJumping;
        if (player.level().isClientSide) {
            // 客户端：通过 keyMapping 获取
            isJumping = net.minecraft.client.Minecraft.getInstance().options.keyJump.isDown();
        } else {
            // 服务端：通过反射获取 jumping 字段
            try {
                java.lang.reflect.Field jumpingField = net.minecraft.world.entity.LivingEntity.class.getDeclaredField("jumping");
                jumpingField.setAccessible(true);
                isJumping = jumpingField.getBoolean(player);
            } catch (Exception e) {
                isJumping = false;
            }
        }

        if (!isJumping) {
            return;
        }

        // ========== 5. 触发推进 ==========
        Vec3 lookVec = player.getLookAngle();
        Vec3 currentVel = player.getDeltaMovement();

        Vec3 newVel = currentVel.add(
                lookVec.x * TURN_SENSITIVITY + (lookVec.x * THRUST_POWER - currentVel.x) * SMOOTH_FACTOR,
                lookVec.y * TURN_SENSITIVITY + (lookVec.y * THRUST_POWER - currentVel.y) * SMOOTH_FACTOR,
                lookVec.z * TURN_SENSITIVITY + (lookVec.z * THRUST_POWER - currentVel.z) * SMOOTH_FACTOR
        );

        player.setDeltaMovement(newVel);

        // ========== 6. 视觉反馈 ==========
        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; i++) {
                double offsetX = (player.getRandom().nextDouble() - 0.5) * 0.5;
                double offsetY = (player.getRandom().nextDouble() - 0.5) * 0.5;
                double offsetZ = (player.getRandom().nextDouble() - 0.5) * 0.5;

                serverLevel.sendParticles(
                        ParticleTypes.FLAME,
                        player.getX() + offsetX,
                        player.getY() + 0.5 + offsetY,
                        player.getZ() + offsetZ,
                        1,
                        -lookVec.x * 0.1,
                        -lookVec.y * 0.1,
                        -lookVec.z * 0.1,
                        0.01
                );
            }
        }

        // ========== 7. 设置冷却 ==========
        COOLDOWNS.put(player, COOLDOWN_TICKS);
    }
}