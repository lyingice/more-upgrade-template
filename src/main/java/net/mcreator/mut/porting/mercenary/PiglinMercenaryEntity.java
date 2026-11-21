package net.mcreator.mut.porting.mercenary;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * 猪灵雇佣兵。
 * <p>
 * 行为分三层：
 * <ol>
 *   <li>身上带了任何金质护甲（原版金甲、鎏金、赤焰金都算）——保持中立，不动手；</li>
 *   <li>没带金甲 —— 当普通敌对生物处理；</li>
 *   <li>玩家穿齐一整套鎏金或赤焰金 —— 反过来帮玩家打架，主动去打玩家正在打的目标
 *       或正在打玩家的目标。</li>
 * </ol>
 */
public class PiglinMercenaryEntity extends Monster {

    /** 原版金甲 */
    private static final Set<Item> VANILLA_GOLD_ARMOR = Set.of(
            Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);

    private static final Set<Item> GILDING_ARMOR = Set.of(
            MutModItems.GILDING_HELMET.get(), MutModItems.GILDING_CHESTPLATE.get(),
            MutModItems.GILDING_LEGGINGS.get(), MutModItems.GILDING_BOOTS.get());

    private static final Set<Item> FLAME_GOLD_ARMOR = Set.of(
            MutModItems.FLAME_GOLD_HELMET.get(), MutModItems.FLAME_GOLD_CHESTPLATE.get(),
            MutModItems.FLAME_GOLD_LEGGINGS.get(), MutModItems.FLAME_GOLD_BOOTS.get());

    /** 帮玩家打架的感应半径（方块） */
    private static final double ALLY_RANGE = 24.0D;

    public PiglinMercenaryEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 12;
        this.setPersistenceRequired();
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ARMOR, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.STEP_HEIGHT, 0.6D);
    }

    // ========== AI ==========
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        // 1. 谁打我，我就打谁
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // 2. 帮穿整套鎏金/赤焰金的玩家打架
        this.targetSelector.addGoal(2, new ProtectAllyGoal(this));
        // 3. 没带金甲的玩家才是敌人
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                living -> living instanceof Player player
                        && !player.isCreative() && !player.isSpectator()
                        && !wearsAnyGoldArmor(player)));
    }

    // ========== 金甲判定 ==========
    /** 任意一件金质护甲（原版金甲 / 鎏金 / 赤焰金），有就不主动打 */
    public static boolean wearsAnyGoldArmor(Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            Item item = player.getItemBySlot(slot).getItem();
            if (VANILLA_GOLD_ARMOR.contains(item) || GILDING_ARMOR.contains(item) || FLAME_GOLD_ARMOR.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /** 穿齐一整套鎏金，或一整套赤焰金 */
    public static boolean wearsFullGoldSet(Player player) {
        return wearsFullSet(player, GILDING_ARMOR) || wearsFullSet(player, FLAME_GOLD_ARMOR);
    }

    private static boolean wearsFullSet(Player player, Set<Item> set) {
        return set.contains(player.getItemBySlot(EquipmentSlot.HEAD).getItem())
                && set.contains(player.getItemBySlot(EquipmentSlot.CHEST).getItem())
                && set.contains(player.getItemBySlot(EquipmentSlot.LEGS).getItem())
                && set.contains(player.getItemBySlot(EquipmentSlot.FEET).getItem());
    }

    // ========== 保护盟友 ==========
    /**
     * 从"被保护的玩家"身上推断出该打谁：
     * 优先打玩家正在打的目标，其次打正在攻击玩家的目标。
     *
     * @return 没有合适目标时返回 null
     */
    public static LivingEntity resolveThreat(Player ally, PiglinMercenaryEntity mercenary) {
        LivingEntity threat = ally.getLastHurtMob();
        if (!isValidThreat(threat, mercenary)) {
            threat = ally.getLastHurtByMob();
        }
        return isValidThreat(threat, mercenary) ? threat : null;
    }

    private static boolean isValidThreat(LivingEntity threat, PiglinMercenaryEntity mercenary) {
        if (threat == null || threat == mercenary || !threat.isAlive()) return false;
        // 不帮玩家打别的玩家
        if (threat instanceof Player) return false;
        return mercenary.distanceToSqr(threat) <= ALLY_RANGE * ALLY_RANGE;
    }

    private static class ProtectAllyGoal extends TargetGoal {

        private final PiglinMercenaryEntity mercenary;
        private Player ally;

        ProtectAllyGoal(PiglinMercenaryEntity mercenary) {
            super(mercenary, false, false);
            this.mercenary = mercenary;
        }

        @Override
        public boolean canUse() {
            this.ally = this.mercenary.level().getNearestPlayer(
                    this.mercenary.getX(), this.mercenary.getY(), this.mercenary.getZ(), ALLY_RANGE,
                    entity -> entity instanceof Player player && wearsFullGoldSet(player));
            if (this.ally == null) return false;

            LivingEntity threat = resolveThreat(this.ally, this.mercenary);
            if (threat == null) return false;

            this.mercenary.setTarget(threat);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mercenary.getTarget();
            return this.ally != null && this.ally.isAlive()
                    && target != null && target.isAlive() && !(target instanceof Player);
        }

        @Override
        public void stop() {
            this.ally = null;
        }
    }
}
