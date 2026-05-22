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
    private int delay = 0;

    public LittleCreeperEntity(EntityType<LittleCreeperEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
    }

    @Override
    protected void registerGoals() {
        // 1. 抓玩家骑乘
        this.goalSelector.addGoal(1, new GrabPlayerGoal(this));
        // 2. 骑上后倒计时引爆
        this.goalSelector.addGoal(2, new ExplodeGoal(this));
        // 3. 水中漂浮
        this.goalSelector.addGoal(3, new FloatGoal(this));
        // 4. 随机游荡
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        // 5. 随机张望
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        // 6. 寻敌
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        // 7. 被攻击反击
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    // =====================================================
    // 抓玩家骑乘
    // =====================================================
    private static class GrabPlayerGoal extends Goal {
        private final LittleCreeperEntity creeper;

        public GrabPlayerGoal(LittleCreeperEntity creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (creeper.isVehicle() || creeper.isPassenger()) {
                return false;
            }
            LivingEntity target = creeper.getTarget();
            if (target instanceof Player player
                    && player.isAlive()
                    && !player.isCreative()
                    && !player.isSpectator()
                    && creeper.distanceToSqr(player) < 4.0) {
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return !creeper.isVehicle()
                    && creeper.getTarget() instanceof Player
                    && creeper.getTarget().isAlive()
                    && creeper.distanceToSqr(creeper.getTarget()) < 9.0;
        }

        @Override
        public void tick() {
            LivingEntity target = creeper.getTarget();
            if (target == null) return;

            Vec3 targetPos = target.position();
            creeper.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0);

            if (creeper.distanceToSqr(target) < 2.0) {
                // 玩家骑上苦力怕
                target.startRiding(creeper, true);
                creeper.delay = 30; // 1.5秒后引爆
            }
        }
    }

    // =====================================================
    // 骑上后倒计时引爆
    // =====================================================
    private static class ExplodeGoal extends Goal {
        private final LittleCreeperEntity creeper;

        public ExplodeGoal(LittleCreeperEntity creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return creeper.isVehicle() && creeper.delay > 0;
        }

        @Override
        public boolean canContinueToUse() {
            return creeper.isAlive() && creeper.isVehicle();
        }

        @Override
        public void tick() {
            // 强制玩家无法脱离
            if (!creeper.getPassengers().isEmpty()) {
                Entity passenger = creeper.getFirstPassenger();
                if (passenger instanceof Player player && player.getVehicle() != creeper) {
                    player.startRiding(creeper, true);
                }
            }

            creeper.delay--;
            if (creeper.delay <= 0 && creeper.getSwellDir() <= 0) {
                creeper.ignite();
                creeper.setSwellDir(1);
            }
        }
    }

    // =====================================================
    // 不可逆膨胀
    // =====================================================
    @Override
    public void setSwellDir(int dir) {
        if (dir == 1 || this.getSwellDir() == 1) {
            super.setSwellDir(1);
        }
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

            if (this.getSwellDir() == -1 && this.getSwelling(0) > 0.1F) {
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
            float swelling = this.getSwelling(1.0F);
            if (swelling >= 1.0F) {
                this.customExplode((ServerLevel) this.level());
            }
        }
    }

    private void customExplode(ServerLevel level) {
        float radius = EXPLOSION_RADIUS;

        this.dead = true;

        level.explode(this, this.getX(), this.getY(), this.getZ(), radius, false, Level.ExplosionInteraction.NONE);

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