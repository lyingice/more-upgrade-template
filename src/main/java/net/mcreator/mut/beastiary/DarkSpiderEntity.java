package net.mcreator.mut.beastiary;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 深暗蜘蛛（蜘蛛女王的子代）。
 * <p>
 * 两个机制：
 * <ol>
 *   <li>咬人附失明：命中后给目标失明，时长按难度 10 / 20 / 30 秒；</li>
 *   <li>当坐骑：主动去扛附近的人形敌对生物——8 格内找一个体型不超过 1.0 的
 *       {@link Monster}（排除一切蜘蛛类），走过去让它 {@code startRiding} 坐到背上，
 *       凑出"亡灵骑蜘蛛"的组合。已经背着人时不会再找。</li>
 * </ol>
 */
public class DarkSpiderEntity extends Spider {

    /** 找坐骑的搜索半径（格） */
    private static final double RIDER_SEARCH_RANGE = 8.0D;
    /** 允许坐上来的人形生物最大体型（碰撞箱宽度，格） */
    private static final float MAX_RIDER_WIDTH = 1.0F;
    /** 骑上去/搜索失败的冷却（tick），避免每 tick 都扫一遍实体列表 */
    private static final int RIDER_SEARCH_COOLDOWN = 100;
    /** 走到多近才让人形生物骑上来（格，平方） */
    private static final double RIDER_MOUNT_DISTANCE_SQR = 2.25D;

    /** 坐骑搜索冷却，0 表示可以搜 */
    private int riderSearchCooldown;

    public DarkSpiderEntity(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.345D) // 比原版蜘蛛（0.3）快 15%
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    // ==================== AI ====================
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 背着人比打架重要，放前面（占用 MOVE，会打断近战）
        this.goalSelector.addGoal(1, new CarryRiderGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ==================== 咬人附失明 ====================
    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) {
            return false;
        }
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, this.blindnessTicks(), 0), this);
        }
        return true;
    }

    /** 失明时长（tick）：和平 / 简单 10 秒，普通 20 秒，困难 30 秒 */
    private int blindnessTicks() {
        return switch (this.level().getDifficulty()) {
            case NORMAL -> 400;
            case HARD -> 600;
            default -> 200; // PEACEFUL 与 EASY 都是 10 秒
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.riderSearchCooldown > 0) {
            this.riderSearchCooldown--;
        }
    }

    // ==================== 当坐骑 ====================
    /**
     * 主动找人形敌对生物来"背"：
     * 扫描 → 走过去 → 让它 startRiding（骑到蜘蛛背上）。
     */
    private static final class CarryRiderGoal extends Goal {

        private final DarkSpiderEntity spider;
        private Monster candidate;

        CarryRiderGoal(DarkSpiderEntity spider) {
            this.spider = spider;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // 已经背着人 / 自己正骑在别的东西上 / 冷却中
            if (this.spider.isVehicle() || this.spider.isPassenger()) {
                return false;
            }
            if (this.spider.riderSearchCooldown > 0) {
                return false;
            }
            // 打架时不去背人，免得把战斗打断
            LivingEntity target = this.spider.getTarget();
            if (target != null && target.isAlive()) {
                return false;
            }

            List<Monster> found = this.spider.level().getEntitiesOfClass(Monster.class,
                    this.spider.getBoundingBox().inflate(RIDER_SEARCH_RANGE),
                    this::isValidRider);
            if (found.isEmpty()) {
                this.spider.riderSearchCooldown = RIDER_SEARCH_COOLDOWN;
                return false;
            }

            // 挑最近的一个
            Monster best = null;
            double bestDistance = Double.MAX_VALUE;
            for (Monster monster : found) {
                double distance = this.spider.distanceToSqr(monster);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = monster;
                }
            }
            if (best == null) {
                return false;
            }
            this.candidate = best;
            return true;
        }

        /** 坐骑候选：活着、不是蜘蛛类、体型够小、自己还没骑东西 */
        private boolean isValidRider(Monster monster) {
            if (monster == this.spider || !monster.isAlive() || monster.isRemoved()) {
                return false;
            }
            if (monster instanceof Spider) {
                return false; // 蜘蛛不骑蜘蛛
            }
            if (monster.isPassenger()) {
                return false; // 已经骑在别的东西上
            }
            if (monster.getBbWidth() > MAX_RIDER_WIDTH) {
                return false; // 体型太大，背不动
            }
            return this.spider.distanceToSqr(monster) <= RIDER_SEARCH_RANGE * RIDER_SEARCH_RANGE;
        }

        @Override
        public boolean canContinueToUse() {
            return this.candidate != null && this.candidate.isAlive() && !this.spider.isVehicle();
        }

        @Override
        public void tick() {
            if (this.candidate == null || !this.candidate.isAlive()) {
                return;
            }
            this.spider.getLookControl().setLookAt(this.candidate, 30.0F, 30.0F);

            if (this.spider.distanceToSqr(this.candidate) > RIDER_MOUNT_DISTANCE_SQR) {
                this.spider.getNavigation().moveTo(this.candidate, 1.0D);
                return;
            }

            // 走到跟前了：让人形生物骑到蜘蛛背上（force = true，无视常规骑乘限制）
            this.spider.getNavigation().stop();
            this.candidate.startRiding(this.spider, true);
            this.spider.riderSearchCooldown = RIDER_SEARCH_COOLDOWN;
            this.candidate = null;
        }

        @Override
        public void stop() {
            this.candidate = null;
            this.spider.getNavigation().stop();
            if (this.spider.riderSearchCooldown <= 0) {
                this.spider.riderSearchCooldown = RIDER_SEARCH_COOLDOWN;
            }
        }
    }
}
