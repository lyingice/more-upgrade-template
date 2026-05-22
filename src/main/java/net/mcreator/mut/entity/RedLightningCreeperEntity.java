package net.mcreator.mut.entity;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.mut.init.MutModEntities;

public class RedLightningCreeperEntity extends Creeper {

    private static final int EXPLOSION_RADIUS = 6;
    private static final float DAMAGE_MULTIPLIER = 3.0F;

    public RedLightningCreeperEntity(EntityType<RedLightningCreeperEntity> type, Level world) {
        super(type, world);
        xpReward = 10;
        setNoAi(false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    // ========== 不可逆膨胀 ==========
    @Override
    public void setSwellDir(int dir) {
        super.setSwellDir(dir);
    }

    // ========== 不被雷劈变种 ==========
    @Override
    public void thunderHit(ServerLevel level, net.minecraft.world.entity.LightningBolt lightningBolt) {
        // 空实现
    }

    // ========== tick ==========
    @Override
    public void tick() {
        if (this.isAlive()) {
            // === 强制寻敌：每秒扫描一次最近玩家 ===
            if (this.getTarget() == null && this.tickCount % 20 == 0) {
                double followRange = this.getAttributeValue(Attributes.FOLLOW_RANGE);
                Player nearest = this.level().getNearestPlayer(this, followRange);
                if (nearest != null && nearest.isAlive() && !nearest.isCreative() && !nearest.isSpectator()) {
                    this.setTarget(nearest);
                }
            }

            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            // 不可逆：已经开始膨胀就不许缩回去
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

    // ========== 自定义爆炸 ==========
    private void customExplode(ServerLevel level) {
        // 基础倍数
        float explosionMultiplier = this.isPowered() ? 2.0F : 1.0F;

        // 如果有坐骑（骑在幻翼上），威力再翻倍
        if (this.isPassenger()) {
            explosionMultiplier *= 2.0F;
        }

        float radius = EXPLOSION_RADIUS * explosionMultiplier;
        float damageMult = DAMAGE_MULTIPLIER * explosionMultiplier;

        this.dead = true;

        // 不破坏方块的爆炸
        level.explode(
                this,
                this.getX(), this.getY(), this.getZ(),
                radius,
                false,
                Level.ExplosionInteraction.NONE
        );

        // 主爆炸冲击波
        level.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                this.getX(), this.getY() + 1, this.getZ(),
                5,
                radius * 0.5, radius * 0.3, radius * 0.5,
                0.5
        );

        // 烟雾粒子
        for (int i = 0; i < 80; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * radius * 1.5;
            double offsetY = (this.random.nextDouble() - 0.5) * radius * 0.8;
            double offsetZ = (this.random.nextDouble() - 0.5) * radius * 1.5;
            level.sendParticles(
                    ParticleTypes.EXPLOSION,
                    this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                    1, 0, 0, 0, 0.1
            );
        }

        // 中心闪光
        for (int i = 0; i < 30; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2;
            double speed = this.random.nextDouble() * radius * 0.7;
            double dx = Math.cos(angle) * speed;
            double dz = Math.sin(angle) * speed;
            level.sendParticles(
                    ParticleTypes.FLASH,
                    this.getX() + dx, this.getY() + this.random.nextDouble() * 2, this.getZ() + dz,
                    1, 0, 0, 0, 0
            );
        }

        // 外圈火花
        for (int i = 0; i < 40; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2;
            double pitch = (this.random.nextDouble() - 0.5) * Math.PI;
            double dist = radius * (0.8 + this.random.nextDouble() * 0.4);
            double dx = Math.cos(angle) * Math.cos(pitch) * dist;
            double dy = Math.sin(pitch) * dist * 0.5;
            double dz = Math.sin(angle) * Math.cos(pitch) * dist;
            level.sendParticles(
                    ParticleTypes.LAVA,
                    this.getX() + dx, this.getY() + dy, this.getZ() + dz,
                    1, dx * 0.2, dy * 0.2, dz * 0.2, 0.5
            );
        }

        // 伤害
        for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(radius))) {
            if (entity instanceof LivingEntity && entity.isAlive() && entity != this) {
                float distance = this.distanceTo(entity);
                if (distance <= radius) {
                    float damageScale = 1.0F - (distance / radius);
                    float damage = damageScale * 15.0F * damageMult;
                    if (damage > 0) {
                        entity.hurt(entity.damageSources().explosion(this, this), damage);
                    }
                }
            }
        }

        // 滞留药水云
        if (!this.getActiveEffects().isEmpty()) {
            net.minecraft.world.entity.AreaEffectCloud cloud = new net.minecraft.world.entity.AreaEffectCloud(
                    this.level(), this.getX(), this.getY(), this.getZ()
            );
            cloud.setRadius(2.5F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10);
            cloud.setDuration(300);
            cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());

            for (net.minecraft.world.effect.MobEffectInstance effect : this.getActiveEffects()) {
                cloud.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect));
            }

            this.level().addFreshEntity(cloud);
        }

        this.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) {
            return false;
        }
        return super.hurt(source, amount);
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

    public static void init(RegisterSpawnPlacementsEvent event) {
        event.register(MutModEntities.RED_LIGHTNING_CREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(world, pos, random)
                        && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 40);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
        return builder;
    }
}