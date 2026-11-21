package net.mcreator.mut.beastiary;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 牙矛：蛮牙军阀投出的投掷物。
 *
 * <p>命中生物造成 9 点伤害，并给一个很大的击退（直接改速度 + 标记同步），随后销毁。
 * 命中方块也直接销毁，不会插在地上留下实体。
 */
public class TuskJavelinEntity extends ThrowableItemProjectile {

    /** 命中伤害 */
    private static final float DAMAGE = 9.0F;

    /** 击退：水平力度与上抛分量 */
    private static final double KNOCKBACK_POWER = 2.0D;
    private static final double KNOCKBACK_LIFT = 0.45D;

    public TuskJavelinEntity(EntityType<? extends TuskJavelinEntity> type, Level level) {
        super(type, level);
    }

    public TuskJavelinEntity(Level level, LivingEntity shooter) {
        super(BeastiaryEntities.TUSK_JAVELIN.get(), shooter, level);
    }

    public TuskJavelinEntity(Level level, double x, double y, double z) {
        super(BeastiaryEntities.TUSK_JAVELIN.get(), x, y, z, level);
    }

    /** 渲染用的物品（牙矛本体） */
    @Override
    protected Item getDefaultItem() {
        return BeastiaryItems.TUSK_JAVELIN.get();
    }

    // ========== 命中 ==========
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) {
            return;
        }
        Entity target = result.getEntity();
        DamageSource source = this.damageSources().thrown(this, this.getOwner());
        target.hurt(source, DAMAGE);
        if (target instanceof LivingEntity living) {
            this.applyKnockback(living);
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    /**
     * 大击退：把目标往远离投掷物的方向推，并抬高一点。
     * 直接改速度后必须置 hurtMarked，客户端才会收到这次位移。
     */
    private void applyKnockback(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-4D) {
            dx = 0.0D;
            dz = 1.0D;
            length = 1.0D;
        }
        Vec3 motion = target.getDeltaMovement();
        target.setDeltaMovement(
                motion.x * 0.2D + dx / length * KNOCKBACK_POWER,
                KNOCKBACK_LIFT,
                motion.z * 0.2D + dz / length * KNOCKBACK_POWER);
        target.hasImpulse = true;
        target.hurtMarked = true;
    }
}
