package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.miracle.MiracleItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 蛮牙军阀（蛮兵要塞首领）。
 *
 * <p>机制：
 * <ul>
 *   <li>近战：{@link MeleeAttackGoal}，主手金斧、攻击力 16；</li>
 *   <li>杀戮之斧：近战命中时 20% 概率直接秒杀目标（创造 / 旁观模式玩家免疫）；</li>
 *   <li>投矛：目标存在且距离 4~24 格时，每 60 tick 投出一支 {@link TuskJavelinEntity}；</li>
 *   <li>受伤限制：玩家造成的伤害单次最多 25 点，猪灵类生物（{@link AbstractPiglin}）造成的伤害 ×5；</li>
 *   <li>死亡必定掉落金质号角。</li>
 * </ul>
 *
 * <p>属性在 BeastiaryEntities 的属性注册处通过 {@link #createAttributes()} 挂载，
 * 本类不自行注册实体类型与属性。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class TuskedWarlordEntity extends Monster {

    /** 杀戮之斧触发概率 */
    private static final float EXECUTION_CHANCE = 0.2F;

    /** 投矛冷却（tick） */
    private static final int JAVELIN_COOLDOWN = 60;

    /** 投矛的最小 / 最大距离（格） */
    private static final double JAVELIN_MIN_RANGE = 4.0D;
    private static final double JAVELIN_MAX_RANGE = 24.0D;

    /** 玩家单次伤害上限 */
    private static final float PLAYER_DAMAGE_CAP = 25.0F;

    /** 猪灵类生物伤害倍率 */
    private static final float PIGLIN_DAMAGE_MULTIPLIER = 5.0F;

    /** 投矛计时器 */
    private int javelinCooldown = 20;

    public TuskedWarlordEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 100;
        this.setPersistenceRequired();
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 750.0D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 35.0D);
    }

    // ========== AI ==========
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        // 谁打我，我就打谁
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // 主动打玩家
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        // 猪灵是它的世仇
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    // ========== 杀戮之斧 ==========
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && !this.level().isClientSide() && target instanceof LivingEntity victim
                && this.canExecute(victim) && this.getRandom().nextFloat() < EXECUTION_CHANCE) {
            this.execute(victim);
        }
        return hurt;
    }

    /** 创造 / 旁观模式玩家不吃秒杀 */
    private boolean canExecute(LivingEntity victim) {
        if (!victim.isAlive()) {
            return false;
        }
        if (victim instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
    }

    /** 直接秒杀：伤害给到远超上限的数值，剩余血量一并算进斩杀线 */
    private void execute(LivingEntity victim) {
        DamageSource source = this.damageSources().mobAttack(this);
        float lethal = (victim.getHealth() + victim.getAbsorptionAmount()) * 8.0F;
        victim.hurt(source, Math.max(lethal, 2000.0F));
        if (victim.isAlive()) {
            // 不死图腾之类兜底一次后仍然要死
            victim.setHealth(0.0F);
            victim.die(source);
        }
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.0F, 0.8F);
    }

    // ========== 投矛 ==========
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.javelinCooldown > 0) {
            this.javelinCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        double distanceSqr = this.distanceToSqr(target);
        if (distanceSqr < JAVELIN_MIN_RANGE * JAVELIN_MIN_RANGE
                || distanceSqr > JAVELIN_MAX_RANGE * JAVELIN_MAX_RANGE) {
            return;
        }
        if (this.javelinCooldown > 0) {
            return;
        }
        this.javelinCooldown = JAVELIN_COOLDOWN;
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.throwJavelin(target);
    }

    private void throwJavelin(LivingEntity target) {
        TuskJavelinEntity javelin = new TuskJavelinEntity(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.4D) - javelin.getY();
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        // 抛物线补偿（12~24 格是主要交战距离），避免远距离砸地
        javelin.shoot(dx, dy + horizontal * 0.08D, dz, 2.2F, 3.0F);
        this.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 0.8F);
        this.level().addFreshEntity(javelin);
    }

    // ========== 死亡掉落 ==========
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        // 100% 掉落金质号角
        this.spawnAtLocation(new ItemStack(MiracleItems.GOLDEN_HORN.get()));
    }

    // ========== 受伤限制（游戏总线） ==========
    /**
     * 玩家打它每下最多 25 点；猪灵打它伤害 ×5（所以猪灵是它真正的克星）。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof TuskedWarlordEntity)) {
            return;
        }
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player) {
            event.setAmount(Math.min(event.getAmount(), PLAYER_DAMAGE_CAP));
        } else if (attacker instanceof AbstractPiglin) {
            event.setAmount(event.getAmount() * PIGLIN_DAMAGE_MULTIPLIER);
        }
    }
}
