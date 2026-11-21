package net.mcreator.mut.beastiary;

import java.util.EnumSet;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 深穴跳蛛（会跳扑、残血会装死后退的洞穴蜘蛛）。
 * <p>
 * 行为链条：
 * <ol>
 *   <li>平时不游荡：刻意没有 {@code RandomStrollGoal} / {@code WaterAvoidingRandomStrollGoal}，
 *       没目标就原地待着；</li>
 *   <li>发现敌人 → 先跳扑一次（{@link LeapAtTargetGoal} 的单次版），然后转为近战；</li>
 *   <li>生命 &lt; 10 → 怯战：给自己 10 秒隐身并朝远离目标的方向后退；</li>
 *   <li>隐身结束 → 清空目标让目标选择器重新索敌，跳扑资格重置，再来一次突袭；</li>
 *   <li>攻击附带中毒 II，时长按难度 2 / 4 / 6 秒。</li>
 * </ol>
 */
public class DeepLeapingSpiderEntity extends CaveSpider {

    /** 怯战血线 */
    private static final float COWARDLY_HEALTH = 10.0F;
    /** 隐身 / 后撤持续 10 秒 */
    private static final int INVISIBLE_TICKS = 200;
    /** 隐身结束后的"自由突袭"窗口，窗口过了如果还残血会再次怯战 */
    private static final int COWARDLY_COOLDOWN = 200;
    /** 后撤跑速 */
    private static final double RETREAT_SPEED = 1.2D;
    /** 后撤目标点离自己多远（格） */
    private static final double RETREAT_DISTANCE = 8.0D;

    /** 本次交战还剩不剩跳扑机会（跳一次就没了） */
    private boolean leapReady = true;
    /** 怯战剩余 tick，&gt; 0 表示正在隐身后退 */
    private int cowardlyTicks;
    /** 怯战冷却，防止刚回来又立刻隐身 */
    private int cowardlyCooldown;
    /** 上一 tick 的目标，用来识别"换了目标 / 重新索敌" */
    private LivingEntity lastTarget;

    public DeepLeapingSpiderEntity(EntityType<? extends CaveSpider> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D) // 比原版蜘蛛（0.3）快 10%
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    // ==================== AI ====================
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 怯战后撤优先级最高：占用 MOVE / LOOK / JUMP，会顶掉跳扑和近战
        this.goalSelector.addGoal(1, new RetreatGoal(this));
        // 一次跳扑（消耗掉 leapReady 后自己让位给近战）
        this.goalSelector.addGoal(2, new OneShotLeapGoal(this, 0.45F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        // 注意：这里故意不加任何 RandomStrollGoal —— 未索敌时不游荡

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ==================== 状态机 ====================
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }

        if (this.cowardlyCooldown > 0) {
            this.cowardlyCooldown--;
        }

        LivingEntity target = this.getTarget();
        // 目标发生变化（重新索敌 / 换人）→ 恢复跳扑资格
        if (target != this.lastTarget) {
            this.lastTarget = target;
            if (target != null) {
                this.leapReady = true;
            }
        }

        if (this.cowardlyTicks > 0) {
            this.cowardlyTicks--;
            if (this.cowardlyTicks <= 0) {
                this.endCowardly();
            }
            return;
        }

        // 残血且冷却好了 → 怯战
        if (target != null && target.isAlive()
                && this.getHealth() < COWARDLY_HEALTH
                && this.cowardlyCooldown <= 0) {
            this.startCowardly();
        }
    }

    /** 进入怯战：10 秒隐身 + 后撤，放弃跳扑 */
    private void startCowardly() {
        this.cowardlyTicks = INVISIBLE_TICKS;
        this.leapReady = false;
        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVISIBLE_TICKS, 0), this);
    }

    /** 隐身结束：清目标重新索敌，跳扑资格重置，还可以再突袭一次 */
    private void endCowardly() {
        this.setTarget(null);
        this.lastTarget = null;
        this.leapReady = true;
        this.cowardlyCooldown = COWARDLY_COOLDOWN;
        this.getNavigation().stop();
    }

    // ==================== 攻击附毒 ====================
    @Override
    public boolean doHurtTarget(Entity target) {
        // 父类（洞穴蜘蛛）会在命中时附加中毒 I，这里再用 forceAddEffect 整条替换成
        // 任务要求的中毒 II，避免"中毒 I 的剩余时长藏在 hiddenEffect 里又冒出来"
        if (!super.doHurtTarget(target)) {
            return false;
        }
        if (target instanceof LivingEntity living) {
            living.forceAddEffect(new MobEffectInstance(MobEffects.POISON, this.poisonTicks(), 1), this);
        }
        return true;
    }

    /** 中毒 II 时长（tick）：和平 / 简单 2 秒，普通 4 秒，困难 6 秒 */
    private int poisonTicks() {
        return switch (this.level().getDifficulty()) {
            case NORMAL -> 80;
            case HARD -> 120;
            default -> 40; // PEACEFUL 与 EASY 都是 2 秒
        };
    }

    // ==================== 一次跳扑 ====================
    /** 只跳一次：消耗 {@link #leapReady} 后不再触发，等重新索敌或隐身结束时补发 */
    private static final class OneShotLeapGoal extends LeapAtTargetGoal {

        private final DeepLeapingSpiderEntity spider;

        OneShotLeapGoal(DeepLeapingSpiderEntity spider, float upwardMotion) {
            super(spider, upwardMotion);
            this.spider = spider;
        }

        @Override
        public boolean canUse() {
            if (!this.spider.leapReady || this.spider.cowardlyTicks > 0) {
                return false;
            }
            LivingEntity target = this.spider.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            // 太远就别把唯一一次跳扑浪费掉（12 格）；2~4 格的起跳窗口由父类自己判断
            if (this.spider.distanceToSqr(target) > 144.0D) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.spider.leapReady = false;
        }
    }

    // ==================== 怯战后撤 ====================
    /** 朝着远离目标的方向后退，并在此期间不看目标以外的东西 */
    private static final class RetreatGoal extends Goal {

        private final DeepLeapingSpiderEntity spider;
        private int pathRefreshTimer;

        RetreatGoal(DeepLeapingSpiderEntity spider) {
            this.spider = spider;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.spider.cowardlyTicks > 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spider.cowardlyTicks > 0;
        }

        @Override
        public void start() {
            this.pathRefreshTimer = 0;
        }

        @Override
        public void tick() {
            // 目标可能已经丢了，退而求其次找最近的玩家当"威胁方向"
            LivingEntity threat = this.spider.getTarget();
            if (threat == null) {
                threat = this.spider.level().getNearestPlayer(this.spider, 16.0D);
            }
            if (threat == null) {
                return;
            }

            // 每 10 tick 重算一次撤退点，避免每 tick 都重建寻路
            if (this.pathRefreshTimer > 0) {
                this.pathRefreshTimer--;
                return;
            }
            this.pathRefreshTimer = 10;

            Vec3 away = new Vec3(this.spider.getX() - threat.getX(), 0.0D, this.spider.getZ() - threat.getZ());
            if (away.lengthSqr() < 1.0E-4D) {
                // 完全重叠时随便挑个方向
                away = new Vec3(this.spider.getRandom().nextDouble() - 0.5D, 0.0D,
                        this.spider.getRandom().nextDouble() - 0.5D);
            }
            away = away.normalize().scale(RETREAT_DISTANCE);
            this.spider.getNavigation().moveTo(
                    this.spider.getX() + away.x,
                    this.spider.getY(),
                    this.spider.getZ() + away.z,
                    RETREAT_SPEED);
        }

        @Override
        public void stop() {
            this.spider.getNavigation().stop();
        }
    }
}
