package net.mcreator.mut.beastiary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 坠骸：带深渊诅咒的骷髅。40 血，基础伤害 2。
 * 自然生成时随机分为近战型与远程型。
 */
public class AbyssSkeletonEntity extends Monster implements RangedAttackMob {

    private static final EntityDataAccessor<Boolean> DATA_RANGED =
            SynchedEntityData.defineId(AbyssSkeletonEntity.class, EntityDataSerializers.BOOLEAN);

    public AbyssSkeletonEntity(EntityType<? extends AbyssSkeletonEntity> type, Level level) {
        super(type, level);
        this.xpReward = 8;
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,
                new ItemStack(Items.BOW));
        this.entityData.set(DATA_RANGED, level.getRandom().nextBoolean());
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_RANGED, false);
    }

    public boolean isRanged() {
        return this.entityData.get(DATA_RANGED);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("AbyssRanged", this.isRanged());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_RANGED, tag.getBoolean("AbyssRanged"));
        if (!this.isRanged()) {
            this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.0D, 40, 15.0F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        Arrow arrow = new Arrow(this.level(), this, new ItemStack(Items.ARROW), new ItemStack(Items.BOW));
        double dx = target.getX() - this.getX();
        double dy = target.getEyeY() - arrow.getY() - 0.1D;
        double dz = target.getZ() - this.getZ();
        double horiz = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + horiz * 0.2D, dz, 1.6F, 6.0F);
        this.level().addFreshEntity(arrow);
    }

    /** 诅咒时长按难度：5 / 10 / 15 秒 */
    public static int curseSeconds(Level level) {
        Difficulty d = level.getDifficulty();
        if (d == Difficulty.HARD) return 15;
        if (d == Difficulty.NORMAL) return 10;
        return 5;
    }
}
