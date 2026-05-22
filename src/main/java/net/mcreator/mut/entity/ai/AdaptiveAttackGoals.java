package net.mcreator.mut.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class AdaptiveAttackGoals {

    /**
     * 创建弩攻击Goal（默认远程，贴脸时暂停射击但不终止）
     */
    public static RangedCrossbowAttackGoal createCrossbowGoal(
            Mob mob, double speed, float rangedRange, float meleeRange) {

        float meleeRangeSqr = meleeRange * meleeRange;

        return new RangedCrossbowAttackGoal(mob, speed, rangedRange) {
            @Override
            public boolean canUse() {
                if (!hasCrossbow(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
                // 贴脸时不启动远程
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                return super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                if (!hasCrossbow(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
                // 贴脸时主动放弃
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                return super.canContinueToUse();
            }
        };
    }

    public static RangedBowAttackGoal createBowGoal(
            Mob mob, double speed, int attackInterval, float rangedRange, float meleeRange) {

        float meleeRangeSqr = meleeRange * meleeRange;

        return new RangedBowAttackGoal(mob, speed, attackInterval, rangedRange) {
            @Override
            public boolean canUse() {
                if (!hasBow(mob) || hasCrossbow(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                return super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                if (!hasBow(mob) || hasCrossbow(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                return super.canContinueToUse();
            }
        };
    }
    public static Goal createTridentGoal(Mob mob, double speed, float rangedRange, float meleeRange) {
        float meleeRangeSqr = meleeRange * meleeRange;

        return new Goal() {
            private int attackCooldown = 0;

            {
                this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            }

            @Override
            public boolean canUse() {
                if (!hasTrident(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null || !target.isAlive()) return false;
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if (!hasTrident(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null || !target.isAlive()) return false;
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                return true;
            }

            @Override
            public void tick() {
                LivingEntity target = mob.getTarget();
                if (target == null) return;

                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                mob.getNavigation().moveTo(target, speed);

                attackCooldown--;
                if (attackCooldown <= 0 && mob.getSensing().hasLineOfSight(target)) {
                    if (mob instanceof RangedAttackMob ranged) {
                        ranged.performRangedAttack(target, 1.0F);
                    }
                    attackCooldown = 30;
                }
            }

            @Override
            public void stop() {
                attackCooldown = 0;
            }
        };
    }

    public static boolean hasTrident(Mob mob) {
        Item main = mob.getMainHandItem().getItem();
        Item off = mob.getItemBySlot(EquipmentSlot.OFFHAND).getItem();
        return main instanceof TridentItem || off instanceof TridentItem;
    }

    /**
     * 创建近战Goal（默认不启用，被近战攻击后才启用）
     */
    public static MeleeAttackGoal createMeleeGoal(
            Mob mob, double speed, float range) {

        float rangeSqr = range * range;

        return new MeleeAttackGoal((net.minecraft.world.entity.PathfinderMob) mob, speed, false) {
            @Override
            protected boolean canPerformAttack(LivingEntity entity) {
                return this.isTimeToAttack()
                        && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth()
                        + entity.getBbWidth() * entity.getBbWidth())
                        && this.mob.getSensing().hasLineOfSight(entity);
            }

            @Override
            public boolean canUse() {
                if (!isMeleeWeapon(mob.getMainHandItem())) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
                // 只在目标贴脸时启用近战
                return mob.distanceToSqr(target) <= rangeSqr;
            }
        };
    }

    // ========== 武器检测 ==========
    public static boolean hasCrossbow(Mob mob) {
        Item main = mob.getMainHandItem().getItem();
        Item off = mob.getItemBySlot(EquipmentSlot.OFFHAND).getItem();
        return main instanceof CrossbowItem || off instanceof CrossbowItem;
    }

    public static boolean hasBow(Mob mob) {
        Item main = mob.getMainHandItem().getItem();
        Item off = mob.getItemBySlot(EquipmentSlot.OFFHAND).getItem();
        return main instanceof BowItem || off instanceof BowItem;
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem;
    }
}