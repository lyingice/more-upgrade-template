package net.mcreator.mut.entity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.mut.init.MutModEntities;

import java.util.EnumSet;

public class LittleCreeperEntity extends Creeper {

    private static final int EXPLOSION_RADIUS = 3;
    /** 一次性爆炸保护，避免 tick 里重复触发 */
    private boolean exploding = false;
    /** 只负责视听效果、不结算伤害的爆炸计算器：伤害由下面按距离手动结算，避免原版伤害叠加一次 */
    private static final net.minecraft.world.level.ExplosionDamageCalculator VISUAL_ONLY =
            new net.minecraft.world.level.ExplosionDamageCalculator() {
                @Override
                public boolean shouldDamageEntity(net.minecraft.world.level.Explosion explosion,
                                                  net.minecraft.world.entity.Entity entity) {
                    return false;
                }

                @Override
                public float getEntityDamageAmount(net.minecraft.world.level.Explosion explosion,
                                                   net.minecraft.world.entity.Entity entity) {
                    return 0.0F;
                }
            };
    private int delay = 0;

    public LittleCreeperEntity(EntityType<LittleCreeperEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
    }

    /** 当前趴在谁的头上；为空表示没抱住人 */
    @javax.annotation.Nullable
    private Player hatTarget;

    public boolean isOnHead() {
        return this.hatTarget != null && this.hatTarget.isAlive();
    }

    @Override
    protected void registerGoals() {
        // 1. 扑向玩家并趴到他头上
        this.goalSelector.addGoal(1, new RidePlayerGoal(this));
        // 2. 水中漂浮
        this.goalSelector.addGoal(3, new FloatGoal(this));
        // 3. 随机游荡
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        // 4. 随机张望
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        // 5. 寻敌
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        // 6. 被攻击反击
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    // =====================================================
    // 趴到玩家头上
    // =====================================================
    /**
     * 原版不支持"生物骑玩家"：Player 没有 PASSENGER 附着点，
     * 直接 startRiding 会在 getPassengerRidingPosition 里抛 IllegalStateException。
     * 所以这里不用骑乘关系，而是每 tick 把苦力怕夹到玩家头顶，并关掉重力。
     */
    public void attachToHead(Player player) {
        this.hatTarget = player;
        this.delay = 30;               // 1.5 秒后引爆
        this.setNoGravity(true);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
    }

    /** 松开：恢复重力、解除膨胀、清零计时 */
    public void releaseFromHead() {
        this.hatTarget = null;
        this.delay = 0;
        this.setNoGravity(false);
        this.setSwellDir(-1);
    }

    /** 把实体摆到玩家头顶（服务端每 tick 调一次，客户端靠位置同步） */
    private void clampToHead(Player player) {
        double headY = player.getY() + player.getEyeHeight() + 0.15D;
        this.setPos(player.getX(), headY, player.getZ());
        this.setDeltaMovement(Vec3.ZERO);
        this.fallDistance = 0.0F;
        this.getNavigation().stop();
    }

    private static class RidePlayerGoal extends Goal {
        private final LittleCreeperEntity creeper;

        RidePlayerGoal(LittleCreeperEntity creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (creeper.isOnHead() || creeper.isPassenger() || creeper.isVehicle()) {
                return false;
            }
            LivingEntity target = creeper.getTarget();
            return target instanceof Player player
                    && player.isAlive()
                    && !player.isCreative()
                    && !player.isSpectator()
                    && player.level() == creeper.level()
                    && creeper.distanceToSqr(player) < 4.0D;
        }

        @Override
        public boolean canContinueToUse() {
            if (creeper.isOnHead()) {
                return false;   // 抱住头部后交给 tick() 维持，不再占用目标
            }
            LivingEntity target = creeper.getTarget();
            return !creeper.isPassenger()
                    && target instanceof Player player
                    && player.isAlive()
                    && !player.isCreative()
                    && !player.isSpectator()
                    && creeper.distanceToSqr(player) < 9.0D;
        }

        @Override
        public void tick() {
            LivingEntity target = creeper.getTarget();
            if (!(target instanceof Player player)) {
                return;
            }
            creeper.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 1.0D);
            if (creeper.distanceToSqr(player) < 2.0D) {
                creeper.attachToHead(player);
            }
        }

        @Override
        public void stop() {
            creeper.getNavigation().stop();
        }
    }

    // =====================================================
    // 不可逆膨胀
    // =====================================================
    @Override
    public void setSwellDir(int dir) {
        // 身上还挂着玩家时不许解除膨胀；没人挂着就允许退回待机
        if (dir == -1 && !this.isIgnited() && this.hatTarget == null) {
            super.setSwellDir(-1);
            return;
        }
        super.setSwellDir(dir);
    }

    // =====================================================
    // 拦截爆炸
    // =====================================================
    @Override
    public void tick() {
        if (this.isAlive()) {
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            int swellDir = this.getSwellDir();
            if (swellDir > 0 && this.getSwelling(0) == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.PRIME_FUSE);
            }
        }

        super.tick();

        if (!this.level().isClientSide() && this.isAlive()) {
            // 抱着头时：每 tick 贴到玩家头顶并推进引信
            if (this.hatTarget != null) {
                Player player = this.hatTarget;
                if (!player.isAlive() || player.isRemoved() || player.level() != this.level()
                        || player.isCreative() || player.isSpectator()) {
                    this.releaseFromHead();
                } else {
                    this.clampToHead(player);
                    if (--this.delay <= 0) {
                        this.setSwellDir(1);
                    }
                }
            }

            float swelling = this.getSwelling(1.0F);
            if (swelling >= 1.0F) {
                this.customExplode((ServerLevel) this.level());
            }
        }
    }

    private void customExplode(ServerLevel level) {
        if (this.exploding) {
            return;
        }
        this.exploding = true;

        float radius = EXPLOSION_RADIUS;

        // 断开抱着头的关系；玩家本体不受影响，只是不再被吸附
        this.hatTarget = null;
        this.ejectPassengers();

        // 视觉/破块用原版爆炸，但实体伤害交给下面按距离结算（VISUAL_ONLY 会返回 0），
        // 否则原版结算一次 + 手动结算一次 = 双倍伤害。
        level.explode(this, this.damageSources().explosion(this, this), VISUAL_ONLY,
                new Vec3(this.getX(), this.getY(), this.getZ()),
                radius, false, Level.ExplosionInteraction.NONE);

        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                this.getX(), this.getY() + 1, this.getZ(),
                3, radius * 0.3, radius * 0.2, radius * 0.3, 0.3);

        for (int i = 0; i < 30; i++) {
            double ox = (this.random.nextDouble() - 0.5) * radius * 1.5;
            double oy = (this.random.nextDouble() - 0.5) * radius * 0.8;
            double oz = (this.random.nextDouble() - 0.5) * radius * 1.5;
            level.sendParticles(ParticleTypes.EXPLOSION,
                    this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                    1, 0, 0, 0, 0.1);
        }

        for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(radius))) {
            if (entity instanceof LivingEntity && entity.isAlive() && entity != this) {
                float distance = this.distanceTo(entity);
                if (distance <= radius) {
                    float damageScale = 1.0F - (distance / radius);
                    float damage = damageScale * 15.0F;
                    if (damage > 0) {
                        entity.hurt(entity.damageSources().explosion(this, this), damage);
                    }
                }
            }
        }

        this.discard();
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        if (damagesource.is(DamageTypes.EXPLOSION) || damagesource.is(DamageTypes.PLAYER_EXPLOSION))
            return false;
        return super.hurt(damagesource, amount);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.creeper.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.creeper.death"));
    }

    // 禁止玩家控制
    @Override
    protected boolean canRide(Entity entity) {
        return true;
    }

    @Override
    protected void doPush(Entity entity) {
        if (!entity.equals(this.getVehicle())) {
            super.doPush(entity);
        }
    }

    public static void init(RegisterSpawnPlacementsEvent event) {
        event.register(MutModEntities.LITTLE_CREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(world, pos, random)
                        && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
        builder = builder.add(Attributes.MAX_HEALTH, 10);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
        return builder;
    }
}