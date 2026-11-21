package net.mcreator.mut.beastiary;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 蜘蛛女王（深暗蛛穴的产卵母体）。
 * <p>
 * 产卵规则：
 * <ol>
 *   <li>条件：附近有生存模式玩家（非创造、非旁观，且不是和平难度）；</li>
 *   <li>读秒：满足条件后每 tick 计一次数，满 300 tick（15 秒）就产卵，然后把计时清零；</li>
 *   <li>打断：读秒期间只要被玩家打过一次，计时立刻从零开始（见 {@link #hurt}）；</li>
 *   <li>产物：在自身周围 3 格内生成 2~3 只 {@link DarkSpiderEntity}。</li>
 * </ol>
 * 由于被玩家打过会清零，实际效果就是"最近 15 秒内没被玩家打过"才会产卵。
 */
public class SpiderQueenEntity extends Spider {

    /** 产卵间隔：15 秒 = 300 tick */
    private static final int SUMMON_INTERVAL = 300;
    /** 触发产卵的玩家感应半径（格） */
    private static final double PLAYER_RANGE = 16.0D;
    /** 子代生成半径（格） */
    private static final double SUMMON_RADIUS = 3.0D;
    /** 一次产卵的最少 / 最多数量 */
    private static final int MIN_SUMMON = 2;
    private static final int MAX_SUMMON = 3;

    /** 产卵读秒；被玩家打到、或附近没有生存模式玩家时归零 */
    private int summonTimer;

    public SpiderQueenEntity(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        // BOSS 级单位不参与自然清除
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // ==================== AI ====================
    // 原版 Spider 的攻击目标是包内私有的 SpiderAttackGoal（外面引用不到），
    // 所以这里重新组一套等价的目标，蜘蛛的爬墙 / 跳扑手感仍然由父类提供。
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ==================== 产卵读秒 ====================
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }

        // 条件不满足（和平难度 / 附近没有生存模式玩家）就暂停读秒并清零
        if (this.level().getDifficulty() == Difficulty.PEACEFUL || !this.hasNearbySurvivalPlayer()) {
            this.summonTimer = 0;
            return;
        }

        if (this.summonTimer < SUMMON_INTERVAL) {
            this.summonTimer++;
            return;
        }

        this.summonTimer = 0;
        this.summonDarkSpiders();
    }

    /** 附近是否有生存模式玩家（死亡 / 创造 / 旁观都不算） */
    private boolean hasNearbySurvivalPlayer() {
        Player player = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), PLAYER_RANGE,
                entity -> entity instanceof Player candidate
                        && candidate.isAlive()
                        && !candidate.isCreative()
                        && !candidate.isSpectator());
        return player != null;
    }

    /** 读秒期间被玩家打到一次，计时从零开始 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && isPlayerSource(source)) {
            this.summonTimer = 0;
        }
        return super.hurt(source, amount);
    }

    /** 伤害是不是玩家造成的（近战 / 弓箭 / 法术都算） */
    private static boolean isPlayerSource(DamageSource source) {
        Entity attacker = source.getEntity();
        return attacker instanceof Player || source.getDirectEntity() instanceof Player;
    }

    // ==================== 产出子代 ====================
    /** 在自身周围 3 格内生成 2~3 只深暗蜘蛛 */
    private void summonDarkSpiders() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int count = MIN_SUMMON + this.getRandom().nextInt(MAX_SUMMON - MIN_SUMMON + 1);
        for (int i = 0; i < count; i++) {
            DarkSpiderEntity spider = BeastiaryEntities.DARK_SPIDER.get().create(serverLevel);
            if (spider == null) {
                continue;
            }

            // 随机落点：1~3 格，绕自身一圈
            double angle = this.getRandom().nextDouble() * Math.PI * 2.0D;
            double distance = 1.0D + this.getRandom().nextDouble() * (SUMMON_RADIUS - 1.0D);
            double x = this.getX() + Math.cos(angle) * distance;
            double z = this.getZ() + Math.sin(angle) * distance;

            spider.moveTo(x, this.getY(), z, this.getYRot(), 0.0F);
            // 子代直接继承女王当前的仇恨目标；不做持久化，玩家走远后能自然清除，避免无限堆积
            spider.setTarget(this.getTarget());
            serverLevel.addFreshEntity(spider);
        }
    }
}
