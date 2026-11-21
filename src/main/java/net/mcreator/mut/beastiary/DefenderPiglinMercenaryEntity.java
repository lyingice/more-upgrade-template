package net.mcreator.mut.beastiary;

import java.util.EnumSet;
import java.util.List;

import net.mcreator.mut.porting.mercenary.PiglinMercenaryEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 防御型雇佣兵：持盾守卫。
 *
 * <p>继承 {@link PiglinMercenaryEntity} 的金甲中立 / 保护玩家逻辑，额外：
 * <ul>
 *   <li>副手常备盾牌，不用时也带在手上；</li>
 *   <li>待命（原地警戒）—— 去掉闲逛目标，只在原地站桩；</li>
 *   <li>敌人进到 3 格内：举盾 + 盾击击退，并强制让敌人把自己设为目标（嘲讽）。</li>
 * </ul>
 */
public class DefenderPiglinMercenaryEntity extends PiglinMercenaryEntity {

    /** 警戒半径（格） */
    private static final double GUARD_RADIUS = 3.0D;

    /** 盾击冷却（tick） */
    private static final int BASH_COOLDOWN = 40;

    /** 盾击伤害 */
    private static final float BASH_DAMAGE = 3.0F;

    /** 盾击的水平推力与上抛分量 */
    private static final double BASH_POWER = 1.6D;
    private static final double BASH_LIFT = 0.45D;

    public DefenderPiglinMercenaryEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PiglinMercenaryEntity.createAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 待命：站桩警戒，不再到处闲逛
        this.goalSelector.removeAllGoals(goal -> goal instanceof WaterAvoidingRandomStrollGoal);
        // 守卫目标排在近战之前：只看 + 举盾 + 盾击
        this.goalSelector.addGoal(1, new ShieldGuardGoal(this));
    }

    // ========== 盾 ==========
    /** 举盾（副手） */
    void raiseShield() {
        if (!this.level().isClientSide() && !this.isUsingItem()) {
            this.startUsingItem(InteractionHand.OFF_HAND);
        }
    }

    /** 收盾 */
    void lowerShield() {
        if (this.isUsingItem() && this.getUsedItemHand() == InteractionHand.OFF_HAND) {
            this.stopUsingItem();
        }
    }

    // ========== 警戒 ==========
    /** 3 格内最近的敌人，没有就返回 null */
    LivingEntity findNearestEnemy() {
        List<LivingEntity> candidates = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(GUARD_RADIUS, 2.0D, GUARD_RADIUS), this::isGuardTarget);
        LivingEntity nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity candidate : candidates) {
            double distance = this.distanceToSqr(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    /** 是否在警戒距离内（水平 3 格 + 高度差不超过 2 格） */
    boolean isWithinGuardRange(LivingEntity enemy) {
        return this.distanceToSqr(enemy) <= GUARD_RADIUS * GUARD_RADIUS
                && Math.abs(enemy.getY() - this.getY()) <= 2.0D;
    }

    /** 谁算敌人：玩家（没穿金甲）、敌对生物；猪灵和雇佣兵同类都不打 */
    private boolean isGuardTarget(LivingEntity candidate) {
        if (candidate == this || !candidate.isAlive() || candidate.isSpectator()) {
            return false;
        }
        if (candidate instanceof PiglinMercenaryEntity || candidate instanceof AbstractPiglin) {
            return false;
        }
        if (candidate instanceof Player player) {
            // 创造 / 旁观玩家永远不算目标（否则盾卫会追着创造模式玩家撞盾）
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
            return !PiglinMercenaryEntity.wearsAnyGoldArmor(player);
        }
        return candidate instanceof Monster;
    }

    // ========== 盾击 ==========
    /** 盾击：造成小伤害 + 击退，并强制敌人锁定自己 */
    void shieldBash(LivingEntity enemy) {
        if (this.level().isClientSide() || enemy == null || !enemy.isAlive()) {
            return;
        }
        double dx = enemy.getX() - this.getX();
        double dz = enemy.getZ() - this.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-4D) {
            dx = 0.0D;
            dz = 1.0D;
            length = 1.0D;
        }
        enemy.hurt(this.damageSources().mobAttack(this), BASH_DAMAGE);
        Vec3 motion = enemy.getDeltaMovement();
        enemy.setDeltaMovement(
                motion.x * 0.2D + dx / length * BASH_POWER,
                BASH_LIFT,
                motion.z * 0.2D + dz / length * BASH_POWER);
        enemy.hasImpulse = true;
        enemy.hurtMarked = true;
        // 嘲讽：强制敌人把自己设为目标
        if (enemy instanceof Mob mob) {
            mob.setLastHurtByMob(this);
            mob.setTarget(this);
        }
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
    }

    /** 守卫目标：举盾盯着最近的敌人，冷却好了就盾击 */
    private static final class ShieldGuardGoal extends Goal {

        private final DefenderPiglinMercenaryEntity defender;
        private LivingEntity enemy;
        private int bashCooldown;

        ShieldGuardGoal(DefenderPiglinMercenaryEntity defender) {
            this.defender = defender;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.defender.level().isClientSide()) {
                return false;
            }
            this.enemy = this.defender.findNearestEnemy();
            return this.enemy != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.enemy != null && this.enemy.isAlive() && this.defender.isWithinGuardRange(this.enemy);
        }

        @Override
        public void start() {
            this.bashCooldown = 0;
            this.defender.raiseShield();
        }

        @Override
        public void stop() {
            this.enemy = null;
            this.defender.lowerShield();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.enemy == null) {
                return;
            }
            this.defender.raiseShield();
            this.defender.getLookControl().setLookAt(this.enemy, 30.0F, 30.0F);
            if (this.bashCooldown > 0) {
                this.bashCooldown--;
                return;
            }
            this.bashCooldown = BASH_COOLDOWN;
            this.defender.shieldBash(this.enemy);
        }
    }
}
