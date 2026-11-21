package net.mcreator.mut.beastiary;

import net.mcreator.mut.porting.mercenary.PiglinMercenaryEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;

/**
 * 远程雇佣兵：弩手。
 *
 * <p>完全继承 {@link PiglinMercenaryEntity} 的金甲中立 / 保护玩家逻辑（目标选择器沿用父类），
 * 只把主手换成弩、加一个远程攻击目标：
 * <ul>
 *   <li>距离 &gt; 4 格 —— 用 {@link RangedAttackGoal} 站着放弩；</li>
 *   <li>贴身 —— 切换到近战目标补刀。</li>
 * </ul>
 */
public class RangedPiglinMercenaryEntity extends PiglinMercenaryEntity implements RangedAttackMob {

    /** 弩矢伤害 */
    private static final double BOLT_DAMAGE = 5.0D;

    /** 远程攻击的最小距离（格），更近就交给近战目标 */
    private static final double MIN_RANGE = 4.0D;

    /** 射击间隔（tick） */
    private static final int ATTACK_INTERVAL = 30;

    /** 远程攻击半径（格） */
    private static final float ATTACK_RADIUS = 15.0F;

    public RangedPiglinMercenaryEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        ItemStack crossbow = new ItemStack(Items.CROSSBOW);
        // 只是让手里的弩看起来是上弦状态（纯客户端表现）
        crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStack(Items.ARROW)));
        this.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PiglinMercenaryEntity.createAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 父类的近战目标让位给弩，贴身时再补回来
        this.goalSelector.removeAllGoals(goal -> goal instanceof MeleeAttackGoal);
        this.goalSelector.addGoal(2, new CrossbowAttackGoal(this, 1.0D, ATTACK_INTERVAL, ATTACK_RADIUS, MIN_RANGE));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
    }

    // ========== 弩射击 ==========
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        AbstractArrow bolt = ProjectileUtil.getMobArrow(this, new ItemStack(Items.ARROW), 1.0F, this.getMainHandItem());
        bolt.setBaseDamage(BOLT_DAMAGE);
        bolt.pickup = AbstractArrow.Pickup.DISALLOWED;
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333333333333333D) - bolt.getY();
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        bolt.shoot(dx, dy + horizontal * 0.2D, dz, 1.8F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        serverLevel.addFreshEntity(bolt);
    }

    /**
     * {@link RangedAttackGoal} 只认"有目标就射"，这里补一个最小距离限制：
     * 敌人贴到 {@code minRange} 以内就让出控制权给近战目标。
     */
    private static final class CrossbowAttackGoal extends RangedAttackGoal {

        private final RangedPiglinMercenaryEntity shooter;
        private final double minRangeSqr;

        CrossbowAttackGoal(RangedPiglinMercenaryEntity shooter, double speedModifier, int attackInterval,
                           float attackRadius, double minRange) {
            super(shooter, speedModifier, attackInterval, attackRadius);
            this.shooter = shooter;
            this.minRangeSqr = minRange * minRange;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.shooter.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            if (this.shooter.distanceToSqr(target) <= this.minRangeSqr) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.shooter.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            if (this.shooter.distanceToSqr(target) <= this.minRangeSqr) {
                return false;
            }
            return super.canContinueToUse();
        }
    }
}
