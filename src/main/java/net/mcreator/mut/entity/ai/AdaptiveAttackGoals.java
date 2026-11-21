package net.mcreator.mut.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

/**
 * 近战 / 远程武器的自适应攻击行为。
 * <p>
 * 统一规则：
 * <ul>
 *   <li>手里有远程武器、目标在近战范围外、且有视线 —— 用远程；</li>
 *   <li>目标进入近战范围 —— 立即切近战，远程主动让位；</li>
 *   <li>没有视线时远程让位，让家伙自己走过去；</li>
 *   <li>只有近战武器时，主动追人（而不是原地打转）。</li>
 * </ul>
 */
public class AdaptiveAttackGoals {

    /** 创建弩攻击Goal（默认远程，贴脸时暂停射击但不终止） */
    public static RangedCrossbowAttackGoal createCrossbowGoal(
            Mob mob, double speed, float rangedRange, float meleeRange) {

        float meleeRangeSqr = meleeRange * meleeRange;

        return new RangedCrossbowAttackGoal(mob, speed, rangedRange) {
            @Override
            public boolean canUse() {
                if (!hasCrossbow(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
                if (mob.distanceToSqr(target) <= meleeRangeSqr) return false;
                if (!mob.getSensing().hasLineOfSight(target)) return false;
                return super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                if (!hasCrossbow(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null) return false;
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
                if (!mob.getSensing().hasLineOfSight(target)) return false;
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
                // 贴脸交给近战 Goal
                return mob.distanceToSqr(target) > meleeRangeSqr;
            }

            @Override
            public boolean canContinueToUse() {
                if (!hasTrident(mob)) return false;
                LivingEntity target = mob.getTarget();
                if (target == null || !target.isAlive()) return false;
                return mob.distanceToSqr(target) > meleeRangeSqr;
            }

            @Override
            public void tick() {
                LivingEntity target = mob.getTarget();
                if (target == null) return;

                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                mob.getNavigation().moveTo(target, speed);

                if (attackCooldown > 0) attackCooldown--;
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
     * 近战Goal。
     * <p>
     * 关键改动：目标在近战范围外时，只要<b>没有远程武器</b>就主动追上去；
     * 有远程武器时把追击让给远程 Goal，避免"原地站着不动也不打"。
     */
    public static MeleeAttackGoal createMeleeGoal(Mob mob, double speed, float range) {
        float rangeSqr = range * range;

        return new MeleeAttackGoal((PathfinderMob) mob, speed, false) {
            @Override
            protected boolean canPerformAttack(LivingEntity entity) {
                double reach = this.mob.getBbWidth() * this.mob.getBbWidth()
                        + entity.getBbWidth() * entity.getBbWidth();
                return this.isTimeToAttack()
                        && this.mob.distanceToSqr(entity) < reach
                        && this.mob.getSensing().hasLineOfSight(entity);
            }

            @Override
            public boolean canUse() {
                if (!isMeleeWeapon(mob.getMainHandItem())) return false;
                LivingEntity target = mob.getTarget();
                if (target == null || !target.isAlive()) return false;
                if (mob.distanceToSqr(target) <= rangeSqr) return true;
                // 没有远程手段时必须自己追过去
                return !hasRangedWeapon(mob);
            }

            @Override
            public boolean canContinueToUse() {
                if (!isMeleeWeapon(mob.getMainHandItem())) return false;
                LivingEntity target = mob.getTarget();
                if (target == null || !target.isAlive()) return false;
                if (mob.distanceToSqr(target) <= rangeSqr) return true;
                return !hasRangedWeapon(mob) && super.canContinueToUse();
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

    /** 手里有没有任何能远程攻击的武器 */
    public static boolean hasRangedWeapon(Mob mob) {
        return hasBow(mob) || hasCrossbow(mob) || hasTrident(mob);
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem;
    }
}
