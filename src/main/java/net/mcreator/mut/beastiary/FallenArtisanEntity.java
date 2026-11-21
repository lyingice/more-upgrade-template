package net.mcreator.mut.beastiary;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 堕化的工匠：模板圣匠的随从，远程投掷工具。
 * <p>
 * 每 50 tick 扔一次，随机从铁剑 / 铁镐 / 铁斧三种变体里挑一个；
 * 手上拿的工具也会跟着变，方便玩家预判下一次扔的是什么。
 */
public class FallenArtisanEntity extends Monster implements RangedAttackMob {

    /** 投掷间隔（tick） */
    private static final int ATTACK_INTERVAL = 50;
    /** 开始投掷的距离 */
    private static final float ATTACK_RADIUS = 18.0F;

    public FallenArtisanEntity(EntityType<? extends FallenArtisanEntity> type, Level level) {
        super(type, level);
        this.xpReward = 6;
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(randomTool(level.getRandom())));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    private static Item randomTool(RandomSource random) {
        return ThrownToolEntity.itemForVariant(random.nextInt(3));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 只靠投掷输出；被贴脸时用走的拉开一点距离
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, ATTACK_INTERVAL, ATTACK_RADIUS));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        int variant = this.random.nextInt(3);
        // 换手上工具，同时把这一发工具换成同一变体
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ThrownToolEntity.itemForVariant(variant)));

        ThrownToolEntity tool = new ThrownToolEntity(this.level(), this, variant);
        double dx = target.getX() - this.getX();
        double dy = target.getEyeY() - tool.getY() - 0.1D;
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        // 抬高一点补偿重力，保证抛物线能落在目标身上
        tool.shoot(dx, dy + horizontal * 0.2D, dz, 1.4F, 5.0F);
        this.level().addFreshEntity(tool);
    }
}
