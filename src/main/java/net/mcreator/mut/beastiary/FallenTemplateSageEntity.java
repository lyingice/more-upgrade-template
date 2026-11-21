package net.mcreator.mut.beastiary;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 堕化的模板圣匠：模板圣庙的远程法系精英。
 * <p>
 * 两套机制：
 * <ol>
 *   <li>远程投掷铁重锤（靠下落高度加伤）；</li>
 *   <li>每 15 秒（300 tick）在身边召唤 1 只堕化的工匠——只有在真正盯上玩家（有玩家仇恨）且
 *       附近工匠不足 4 只时才会召唤，避免无限刷屏。</li>
 * </ol>
 */
public class FallenTemplateSageEntity extends Monster implements RangedAttackMob {

    /** 召唤间隔：15 秒 */
    private static final int SUMMON_INTERVAL = 300;
    /** 场上最多同时存在的小怪数量 */
    private static final int MAX_ARTISANS = 4;
    /** 统计"场上已有多少工匠"的半径 */
    private static final double SUMMON_CHECK_RADIUS = 16.0D;
    /** 召唤落点距本体的距离 */
    private static final double SUMMON_DISTANCE = 2.5D;

    private int summonCooldown = SUMMON_INTERVAL;

    public FallenTemplateSageEntity(EntityType<? extends FallenTemplateSageEntity> type, Level level) {
        super(type, level);
        this.xpReward = 30;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 60, 22.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ========== 远程：投铁重锤 ==========
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        IronHammerEntity hammer = new IronHammerEntity(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getEyeY() - hammer.getY() - 0.1D;
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        // 稍微上扬，让锤子先升后落，命中时才有额外的下落加成
        hammer.shoot(dx, dy + horizontal * 0.25D, dz, 1.3F, 4.0F);
        this.level().addFreshEntity(hammer);

        this.level().playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW,
                net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 0.7F);
    }

    // ========== 召唤堕化的工匠 ==========
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.summonCooldown > 0) {
            this.summonCooldown--;
            return;
        }
        this.summonCooldown = SUMMON_INTERVAL;
        if (this.canSummon()) {
            this.summonArtisan();
        }
    }

    /** 只在有玩家仇恨、且附近工匠不到 4 只时才召唤 */
    private boolean canSummon() {
        LivingEntity target = this.getTarget();
        if (!(target instanceof Player) || !target.isAlive()) {
            return false;
        }
        List<FallenArtisanEntity> artisans = this.level()
                .getEntitiesOfClass(FallenArtisanEntity.class, this.getBoundingBox().inflate(SUMMON_CHECK_RADIUS));
        return artisans.size() < MAX_ARTISANS;
    }

    private void summonArtisan() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        FallenArtisanEntity artisan = BeastiaryEntities.FALLEN_ARTISAN.get().create(serverLevel);
        if (artisan == null) {
            return;
        }
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double x = this.getX() + Math.cos(angle) * SUMMON_DISTANCE;
        double z = this.getZ() + Math.sin(angle) * SUMMON_DISTANCE;
        artisan.moveTo(x, this.getY(), z, this.getYRot(), 0.0F);
        // 召唤物直接接上本体的仇恨目标，落地就开始干活
        artisan.setTarget(this.getTarget());
        artisan.setPersistenceRequired();
        serverLevel.addFreshEntity(artisan);

        serverLevel.sendParticles(ParticleTypes.SOUL, x, this.getY() + 1.0D, z, 24, 0.4D, 0.6D, 0.4D, 0.02D);
        this.level().playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL,
                net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 0.9F);
    }
}
