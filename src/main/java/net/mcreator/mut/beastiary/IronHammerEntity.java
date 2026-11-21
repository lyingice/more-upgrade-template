package net.mcreator.mut.beastiary;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 堕化的模板圣匠投出的铁重锤。
 * <p>
 * 伤害跟"砸下来的高度"绑定：基础 6 点，每下落 1 格 +2 点，封顶 40 点。
 * 所以从高处（或抛物线顶点往下）砸中的伤害明显更高，平射只有基础伤害。
 */
public class IronHammerEntity extends ThrowableItemProjectile {

    /** 命中基础伤害 */
    private static final float BASE_DAMAGE = 6.0F;
    /** 每下落一格追加的伤害 */
    private static final float DAMAGE_PER_BLOCK = 2.0F;
    /** 伤害上限 */
    private static final float MAX_DAMAGE = 40.0F;

    /** 投出瞬间的 y 坐标；NaN = 还没记录（首个 tick 兜底记录） */
    private double startY = Double.NaN;

    public IronHammerEntity(EntityType<? extends IronHammerEntity> type, Level level) {
        super(type, level);
    }

    /** 供生物投掷时用的便捷构造（会顺手把渲染用物品和起点 y 都设好） */
    public IronHammerEntity(Level level, LivingEntity shooter) {
        super(BeastiaryEntities.IRON_HAMMER.get(), shooter, level);
        this.setItem(new ItemStack(BeastiaryItems.IRON_HAMMER.get()));
        this.markLaunchPosition(shooter.getEyeY() - 0.1D);
    }

    @Override
    protected Item getDefaultItem() {
        return BeastiaryItems.IRON_HAMMER.get();
    }

    /** 记录投出时的 y 坐标，下落距离以它为起点 */
    public void markLaunchPosition(double y) {
        this.startY = y;
    }

    /** 已经下落的高度（格），不会为负 */
    public double getFallDistanceInBlocks() {
        if (Double.isNaN(this.startY)) {
            return 0.0D;
        }
        return Math.max(0.0D, this.startY - this.getY());
    }

    /** 按当前下落高度算出本次命中的伤害 */
    public float getFallDamage() {
        float damage = BASE_DAMAGE + DAMAGE_PER_BLOCK * (float) this.getFallDistanceInBlocks();
        return Math.min(damage, MAX_DAMAGE);
    }

    @Override
    public void tick() {
        // 兜底：任何路径生成的锤子（指令、存档读回）都以首个 tick 的位置作为起点
        if (Double.isNaN(this.startY)) {
            this.startY = this.getY();
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        target.hurt(this.damageSources().thrown(this, this.getOwner()), this.getFallDamage());
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.level().playSound(null, this.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.NEUTRAL,
                    0.8F, 0.7F + this.random.nextFloat() * 0.2F);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            // 砸碎的铁屑
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, this.getItem());
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(),
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!Double.isNaN(this.startY)) {
            tag.putDouble("HammerStartY", this.startY);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HammerStartY")) {
            this.startY = tag.getDouble("HammerStartY");
        }
    }
}
