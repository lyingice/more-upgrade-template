package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.mcreator.mut.init.MutModParticles;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ISonicBoomSword {

    /**
     * 每次使用消耗的基础耐久
     */
    default int getBaseDurabilityCost() {
        return 10;
    }

    /**
     * 额外消耗的最大耐久百分比（0.02 = 2%）
     */
    default float getMaxDurabilityPercentCost() {
        return 0.02F;
    }

    /**
     * 冷却时间（刻）
     */
    default int getCooldownTicks() {
        return 200; // 30秒
    }

    /**
     * 声波攻击范围（格）
     */
    default float getSonicRange() {
        return 15F;
    }

    /**
     * 伤害倍数
     */
    default float getDamageMultiplier() {
        return 1.5F;
    }

    /**
     * 使用声波攻击，在 SwordItem 的 use 方法里调用
     */
    default InteractionResultHolder<ItemStack> useSonicBoom(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof SwordItem)
                && !(stack.getItem() instanceof BaseMaceItem)) {
            return InteractionResultHolder.fail(stack);
        }

        // 计算耐久消耗
        int baseCost = getBaseDurabilityCost();
        int maxDurability = stack.getMaxDamage();
        int percentCost = (int) (maxDurability * getMaxDurabilityPercentCost());
        int totalCost = baseCost + percentCost;

        // 检查耐久
        int remainingDurability = maxDurability - stack.getDamageValue();
        if (remainingDurability <= totalCost) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("耐久不足！需要 " + totalCost + " 点耐久"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // 冷却检查（用物品 NBT 存储冷却时间戳）
        // 这里简化：用玩家 cooldown
        if (!level.isClientSide()) {
            // 执行声波攻击
            performSonicBoom(level, player);

            // 消耗耐久
            stack.hurtAndBreak(totalCost, player, LivingEntity.getSlotForHand(hand));

            // 设置冷却
            player.getCooldowns().addCooldown(stack.getItem(), getCooldownTicks());
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /**
     * 执行声波攻击
     */
    default void performSonicBoom(Level level, Player player) {
        Vec3 from = player.position().add(0, player.getEyeHeight() * 0.8, 0);
        Vec3 look = player.getLookAngle();
        Vec3 to = from.add(look.scale(getSonicRange()));

        // 伤害计算
        ItemStack stack = player.getMainHandItem();
        float baseDamage = (float) player.getAttributeValue(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        float damage = baseDamage * getDamageMultiplier();

        // 粒子
        if (level instanceof ServerLevel sl) {
            for (int i = 1; i <= getSonicRange(); i++) {
                Vec3 point = from.add(look.scale(i));
                sl.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                        point.x, point.y, point.z,
                        1, 0, 0, 0, 0
                );
            }

            // 穿透伤害
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().expandTowards(look.scale(getSonicRange())).inflate(1.0))) {
                if (target != player && target.isAlive()) {
                    Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                    if (distanceToRay(from, look, targetPos) < 1.5) {
                        target.invulnerableTime = 0;
                        target.hurt(player.damageSources().sonicBoom(player), damage);
                        target.invulnerableTime = 0;
                    }
                }
            }
            sl.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM, player.getSoundSource(), 1.5F, 1.0F);
        }
    }

    /**
     * 计算点到射线的距离
     */
    default double distanceToRay(Vec3 rayOrigin, Vec3 rayDir, Vec3 point) {
        Vec3 toPoint = point.subtract(rayOrigin);
        double t = toPoint.dot(rayDir);
        if (t < 0) return rayOrigin.distanceTo(point);
        Vec3 closest = rayOrigin.add(rayDir.scale(t));
        return closest.distanceTo(point);
    }
}