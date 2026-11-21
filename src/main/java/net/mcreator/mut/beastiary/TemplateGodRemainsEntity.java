package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.EnumSet;

/**
 * 模板之神的残躯：模板圣庙的最终 BOSS 躯壳。
 * <p>
 * 伤害模型刻意做成"磨血型"：
 * <ul>
 *   <li>任何单次伤害最多 10 点（多段高爆发也打不穿）；</li>
 *   <li>每次受伤后 20 tick（1 秒）完全免疫，期间所有伤害都被
 *       {@link LivingIncomingDamageEvent} 直接取消；</li>
 *   <li>近战带追击：目标在中距离时会蹬地突进一次（80 tick 冷却）。</li>
 * </ul>
 * 400 血 + 10 点/秒的伤害上限，理论最短击杀时间约 40 秒。
 */
public class TemplateGodRemainsEntity extends Monster {

    /** 单次受到的伤害上限 */
    public static final float MAX_SINGLE_HIT = 10.0F;
    /** 受伤后的无敌时长（1 秒） */
    public static final int IMMUNE_TICKS = 20;
    /** 突进冷却（4 秒） */
    private static final int DASH_COOLDOWN = 80;
    /** 突进触发的最小 / 最大距离（格） */
    private static final double DASH_MIN_DISTANCE_SQR = 9.0D;
    private static final double DASH_MAX_DISTANCE_SQR = 400.0D;

    /** 剩余无敌 tick，> 0 时免疫一切（不穿透无敌的）伤害 */
    private int immuneTicks;
    /** 突进剩余冷却 */
    private int dashCooldown;

    public TemplateGodRemainsEntity(EntityType<? extends TemplateGodRemainsEntity> type, Level level) {
        super(type, level);
        this.xpReward = 200;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 追击优先于普通近战：中距离先突进，贴脸再用近战磨
        this.goalSelector.addGoal(2, new DashGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ========== 计时器 ==========
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.immuneTicks > 0) {
            this.immuneTicks--;
        }
        if (this.dashCooldown > 0) {
            this.dashCooldown--;
        }
    }

    public int getImmuneTicks() {
        return this.immuneTicks;
    }

    // ========== 伤害规则（第一道闸：hurt 里先砍） ==========
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) {
            return false;
        }
        // 无敌窗口内完全免疫（/kill、虚空等穿透无敌的来源除外）
        if (this.immuneTicks > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        boolean hurt = super.hurt(source, Math.min(amount, MAX_SINGLE_HIT));
        if (hurt) {
            this.immuneTicks = IMMUNE_TICKS;
        }
        return hurt;
    }

    // ========== 伤害规则（第二道闸：事件层再兜一次，含取消） ==========
    /**
     * 给事件监听器调用的处理逻辑：无敌期取消，否则把伤害压到 10 点以内。
     * 与 {@link #hurt} 保持同一套规则，任何绕过 hurt 的伤害也会被这里挡住。
     */
    void applyDamageRules(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        if (this.immuneTicks > 0) {
            event.setCanceled(true);
            return;
        }
        if (event.getAmount() > MAX_SINGLE_HIT) {
            event.setAmount(MAX_SINGLE_HIT);
        }
    }

    /** 全局监听：残躯的无敌窗口直接取消伤害事件 */
    @EventBusSubscriber(modid = MutMod.MODID)
    public static final class DamageRules {

        private DamageRules() {
        }

        @SubscribeEvent
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            if (event.getEntity() instanceof TemplateGodRemainsEntity remains) {
                remains.applyDamageRules(event);
            }
        }
    }

    // ========== 追击 ==========
    /** 中距离蹬地突进，缩短与目标的距离 */
    private static class DashGoal extends Goal {

        private final TemplateGodRemainsEntity remains;

        DashGoal(TemplateGodRemainsEntity remains) {
            this.remains = remains;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.remains.getTarget();
            if (target == null || !target.isAlive() || this.remains.dashCooldown > 0) {
                return false;
            }
            double distanceSqr = this.remains.distanceToSqr(target);
            if (distanceSqr < DASH_MIN_DISTANCE_SQR || distanceSqr > DASH_MAX_DISTANCE_SQR) {
                return false;
            }
            return this.remains.hasLineOfSight(target);
        }

        @Override
        public void start() {
            LivingEntity target = this.remains.getTarget();
            if (target == null) {
                return;
            }
            Vec3 direction = new Vec3(target.getX() - this.remains.getX(), 0.0D, target.getZ() - this.remains.getZ());
            if (direction.lengthSqr() > 1.0E-4D) {
                direction = direction.normalize().scale(1.1D);
            }
            // 水平猛冲 + 一点上抛，落下时正好砸向目标
            this.remains.setDeltaMovement(direction.x, 0.45D, direction.z);
            this.remains.hasImpulse = true;
            this.remains.dashCooldown = DASH_COOLDOWN;

            if (this.remains.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.remains.getX(),
                        this.remains.getY() + 0.2D, this.remains.getZ(), 20, 0.5D, 0.1D, 0.5D, 0.02D);
            }
            this.remains.playSound(SoundEvents.ENDER_DRAGON_FLAP, 0.8F, 0.6F);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}
