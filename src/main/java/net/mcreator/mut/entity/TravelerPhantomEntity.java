package net.mcreator.mut.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import net.mcreator.mut.init.MutModEntities;

import java.util.EnumSet;
import java.util.List;

public class TravelerPhantomEntity extends Monster {

    private int grabCooldown = 0;
    private int carryDuration = 0;
    private static final int MAX_CARRY_TIME = 200;
    private static final int GRAB_COOLDOWN_TIME = 100;
    public RedLightningCreeperEntity reservedCreeper = null;

    public TravelerPhantomEntity(EntityType<TravelerPhantomEntity> type, Level world) {
        super(type, world);
        xpReward = 10;
        setNoAi(false);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 0. 背上苦力怕时殉爆攻击（最高优先级）
        this.goalSelector.addGoal(0, new CarryCreeperToPlayerGoal(this));
        // 1. 寻找苦力怕并骑上去
        this.goalSelector.addGoal(1, new FindCreeperAndMountGoal(this));
        // 2. 抓取骑乘玩家
        this.goalSelector.addGoal(2, new GrabAndCarryGoal(this));
        // 3. 追击目标
        this.goalSelector.addGoal(3, new ChaseAndAttackGoal(this));
        // 4. 主动寻敌（不需要视线）
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        // 5. 被攻击反击
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        // 6. 随机飞行游荡
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = TravelerPhantomEntity.this.getRandom();
                double dir_x = TravelerPhantomEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = TravelerPhantomEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = TravelerPhantomEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }
        });
        // 7. 随机张望
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // =====================================================
    // 殉爆AI：骑上苦力怕后带它冲向玩家
    // =====================================================
    private static class CarryCreeperToPlayerGoal extends Goal {
        private final TravelerPhantomEntity phantom;

        public CarryCreeperToPlayerGoal(TravelerPhantomEntity phantom) {
            this.phantom = phantom;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = phantom.getTarget();
            if (target instanceof Player player
                    && (player.isCreative() || player.isSpectator())) {
                return false;
            }
            return phantom.getFirstPassenger() instanceof RedLightningCreeperEntity
                    && target != null
                    && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = phantom.getTarget();
            if (target instanceof Player player
                    && (player.isCreative() || player.isSpectator())) {
                return false;
            }
            return phantom.getFirstPassenger() instanceof RedLightningCreeperEntity
                    && target != null
                    && target.isAlive()
                    && phantom.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = phantom.getTarget();
            if (target == null) return;

            // 幻翼只负责冲向玩家，不主动点燃苦力怕
            // 苦力怕自己靠 SwellGoal 决定什么时候膨胀
            Vec3 targetPos = target.position();
            phantom.moveControl.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 2.0);
        }
    }

    // =====================================================
    // 寻找苦力怕并骑上去
    // =====================================================
    private static class FindCreeperAndMountGoal extends Goal {
        private final TravelerPhantomEntity phantom;
        private RedLightningCreeperEntity targetCreeper;
        private int searchCooldown = 0;

        public FindCreeperAndMountGoal(TravelerPhantomEntity phantom) {
            this.phantom = phantom;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (phantom.isVehicle() || phantom.isPassenger()) {
                return false;
            }
            if (phantom.getTarget() != null) {
                return false;
            }
            if (phantom.grabCooldown > 0) {
                return false;
            }
            // 冷却控制扫描频率
            if (searchCooldown > 0) {
                searchCooldown--;
                return false;
            }

            List<RedLightningCreeperEntity> creepers = phantom.level()
                    .getEntitiesOfClass(RedLightningCreeperEntity.class,
                            phantom.getBoundingBox().inflate(64));

            for (RedLightningCreeperEntity creeper : creepers) {
                if (creeper.isAlive()
                        && !creeper.isVehicle()
                        && !creeper.isPassenger()) {
                    // 检查是否被其他幻翼预定
                    boolean alreadyReserved = false;
                    for (TravelerPhantomEntity otherPhantom : phantom.level()
                            .getEntitiesOfClass(TravelerPhantomEntity.class,
                                    phantom.getBoundingBox().inflate(64))) {
                        if (otherPhantom.reservedCreeper == creeper) {
                            alreadyReserved = true;
                            break;
                        }
                    }
                    if (!alreadyReserved) {
                        targetCreeper = creeper;
                        phantom.reservedCreeper = creeper;  // 预定，不设target
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (targetCreeper == null || !targetCreeper.isAlive()) {
                return false;
            }
            // 苦力怕已经被别人骑了 → 放弃，重新找
            if (targetCreeper.isPassenger() && targetCreeper.getVehicle() != phantom) {
                return false;
            }
            // 还没骑上，继续
            return !phantom.isVehicle() && phantom.getTarget() == null;
        }

        @Override
        public void tick() {
            if (targetCreeper == null) return;

            Vec3 targetPos = targetCreeper.position().add(0, 2, 0);
            phantom.moveControl.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 1.0);

            if (phantom.distanceToSqr(targetCreeper) < 4.0) {
                targetCreeper.startRiding(phantom, true);
                phantom.reservedCreeper = null;  // 清除预定

                Player nearest = phantom.level().getNearestPlayer(phantom, 32);
                if (nearest != null && nearest.isAlive()) {
                    phantom.setTarget(nearest);
                }
            }
        }

        @Override
        public void stop() {
            if (targetCreeper != null && targetCreeper.getVehicle() != phantom) {
                phantom.reservedCreeper = null;  // 释放预定
            }
            targetCreeper = null;
            searchCooldown = 40;
        }
    }

    // =====================================================
    // 抓取骑乘AI
    // =====================================================
    private static class GrabAndCarryGoal extends Goal {
        private final TravelerPhantomEntity phantom;
        private Player targetPlayer;
        private double targetY;

        public GrabAndCarryGoal(TravelerPhantomEntity phantom) {
            this.phantom = phantom;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // 已经在骑乘或被骑乘 → 不触发
            if (phantom.grabCooldown > 0 || phantom.isVehicle() || phantom.isPassenger()) {
                return false;
            }

            // 场上还有空闲苦力怕 → 不抓玩家，优先等苦力怕
            if (hasFreeCreeperNearby()) {
                return false;
            }

            if (phantom.getTarget() instanceof Player player
                    && player.isAlive()
                    && !player.isCreative()
                    && !player.isSpectator()
                    && phantom.distanceToSqr(player) < 9.0) {

                if (phantom.getRandom().nextFloat() < 0.5F) {
                    this.targetPlayer = player;
                    this.targetY = player.getY() + 100;
                    return true;
                }
            }
            return false;
        }

        // 检查周围是否有空闲苦力怕
        private boolean hasFreeCreeperNearby() {
            List<RedLightningCreeperEntity> creepers = phantom.level()
                    .getEntitiesOfClass(RedLightningCreeperEntity.class,
                            phantom.getBoundingBox().inflate(32));
            for (RedLightningCreeperEntity creeper : creepers) {
                if (creeper.isAlive()
                        && !creeper.isVehicle()
                        && !creeper.isPassenger()) {
                    return true; // 还有空闲苦力怕，不抓玩家
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return targetPlayer != null
                    && targetPlayer.isAlive()
                    && !targetPlayer.isCreative()
                    && !targetPlayer.isSpectator()
                    && phantom.getY() < targetY;
        }

        @Override
        public void start() {
            phantom.carryDuration = 0;
            if (targetPlayer != null) {
                targetPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 240, 0));
                targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 240, 0));
                targetPlayer.startRiding(phantom, true);
            }
        }

        @Override
        public void tick() {
            if (targetPlayer == null || !targetPlayer.isAlive()
                    || targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                return;
            }

            if (targetPlayer.getVehicle() != phantom) {
                targetPlayer.startRiding(phantom, true);
            }

            Vec3 currentPos = phantom.position();
            phantom.moveControl.setWantedPosition(currentPos.x, targetY, currentPos.z, 1.5);
        }

        @Override
        public void stop() {
            if (targetPlayer != null) {
                targetPlayer.stopRiding();
            }
            phantom.grabCooldown = GRAB_COOLDOWN_TIME;
            phantom.carryDuration = 0;
            this.targetPlayer = null;
        }
    }

    // =====================================================
    // 追击攻击AI
    // =====================================================
    private static class ChaseAndAttackGoal extends Goal {
        private final TravelerPhantomEntity phantom;

        public ChaseAndAttackGoal(TravelerPhantomEntity phantom) {
            this.phantom = phantom;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = phantom.getTarget();
            // 排除创造/旁观玩家
            if (target instanceof Player player
                    && (player.isCreative() || player.isSpectator())) {
                return false;
            }
            return target != null
                    && target.isAlive()
                    && !phantom.isVehicle()
                    && !phantom.isPassenger()
                    && phantom.grabCooldown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = phantom.getTarget();
            if (target instanceof Player player
                    && (player.isCreative() || player.isSpectator())) {
                return false;
            }
            return target != null
                    && target.isAlive()
                    && !phantom.isPassenger()
                    && phantom.distanceToSqr(target) > 4.0;
        }

        @Override
        public void tick() {
            LivingEntity target = phantom.getTarget();
            if (target != null) {
                Vec3 targetPos = target.position().add(0, 2.0, 0);
                phantom.moveControl.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 1.2);

                if (phantom.getBoundingBox().inflate(0.3).intersects(target.getBoundingBox())) {
                    phantom.doHurtTarget(target);
                }
            }
        }
    }

    // ========== tick ==========
    @Override
    public void tick() {
        super.tick();

        if (this.grabCooldown > 0) {
            this.grabCooldown--;
        }

        if (this.carryDuration >= MAX_CARRY_TIME && this.isVehicle()) {
            this.ejectPassengers();
            this.carryDuration = 0;
            this.grabCooldown = GRAB_COOLDOWN_TIME;
        }
    }

    // ========== 音效 ==========
    @Override
    public SoundEvent getAmbientSound() {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.phantom.swoop"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.phantom.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.phantom.death"));
    }

    // ========== 伤害免疫 ==========
    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        if (damagesource.is(DamageTypes.FALL))
            return false;
        if (damagesource.is(DamageTypes.LIGHTNING_BOLT))
            return false;
        if (damagesource.is(DamageTypes.EXPLOSION)
                || damagesource.is(DamageTypes.PLAYER_EXPLOSION))
            return false;
        return super.hurt(damagesource, amount);
    }

    // ========== 飞行移动 ==========
    @Override
    public void travel(Vec3 dir) {
        this.travelFlying(dir);
    }

    private void travelFlying(Vec3 dir) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, dir);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, dir);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        } else {
            this.moveRelative((float) this.getAttributeValue(Attributes.FLYING_SPEED), dir);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

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

    // ========== 生成注册 ==========
    public static void init(RegisterSpawnPlacementsEvent event) {
        event.register(MutModEntities.TRAVELER_PHANTOM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(world, pos, random)
                        && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 20);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
        builder = builder.add(Attributes.FLYING_SPEED, 0.25);
        return builder;
    }
}