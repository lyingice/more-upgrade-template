package net.mcreator.mut.beastiary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 堕化的工匠投出的工具（铁剑 / 铁镐 / 铁斧三种变体）。
 * <ul>
 *   <li>变体 0 铁剑：20 点伤害；</li>
 *   <li>变体 1 铁镐：对目标身上每件盔甲各造成 10~20 点耐久损伤，并补 5 点伤害；</li>
 *   <li>变体 2 铁斧：35 点伤害；若目标正举盾，额外让盾牌进 5 秒冷却（伤害照吃，斧子破防）。</li>
 * </ul>
 */
public class ThrownToolEntity extends ThrowableItemProjectile {

    public static final int VARIANT_SWORD = 0;
    public static final int VARIANT_PICKAXE = 1;
    public static final int VARIANT_AXE = 2;

    /** 变体（会同步到客户端，渲染时按变体取对应物品模型） */
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(ThrownToolEntity.class, EntityDataSerializers.INT);

    private static final float SWORD_DAMAGE = 20.0F;
    private static final float PICKAXE_BONUS_DAMAGE = 5.0F;
    private static final int PICKAXE_DURABILITY_MIN = 10;
    private static final int PICKAXE_DURABILITY_MAX = 20;
    private static final float AXE_DAMAGE = 35.0F;
    /** 破盾后盾牌冷却：5 秒 */
    private static final int SHIELD_COOLDOWN_TICKS = 100;

    public ThrownToolEntity(EntityType<? extends ThrownToolEntity> type, Level level) {
        super(type, level);
    }

    /** 供生物投掷时用的便捷构造 */
    public ThrownToolEntity(Level level, LivingEntity shooter, int variant) {
        super(BeastiaryEntities.THROWN_TOOL.get(), shooter, level);
        this.setVariant(variant);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, VARIANT_SWORD);
    }

    @Override
    protected Item getDefaultItem() {
        return BeastiaryItems.THROWN_TOOL.get();
    }

    /** 渲染/粒子用：按变体给出原版工具物品 */
    public static Item itemForVariant(int variant) {
        if (variant == VARIANT_PICKAXE) {
            return Items.IRON_PICKAXE;
        }
        if (variant == VARIANT_AXE) {
            return Items.IRON_AXE;
        }
        return Items.IRON_SWORD;
    }

    /** 渲染时复用的显示物品，避免每帧 new 一个 ItemStack */
    private ItemStack displayStack;

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, Math.max(VARIANT_SWORD, Math.min(VARIANT_AXE, variant)));
    }

    @Override
    public ItemStack getItem() {
        // 三种变体共用同一个注册物品，这里按变体换成对应的原版工具，
        // 让投掷物在飞行时看起来就是铁剑/铁镐/铁斧。
        Item wanted = itemForVariant(this.getVariant());
        if (this.displayStack == null || this.displayStack.getItem() != wanted) {
            this.displayStack = new ItemStack(wanted);
        }
        return this.displayStack;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        int variant = this.getVariant();
        if (variant == VARIANT_PICKAXE) {
            this.damageArmor(target);
            target.hurt(this.damageSources().thrown(this, this.getOwner()), PICKAXE_BONUS_DAMAGE);
        } else if (variant == VARIANT_AXE) {
            this.breakShield(target);
            target.hurt(this.damageSources().thrown(this, this.getOwner()), AXE_DAMAGE);
        } else {
            target.hurt(this.damageSources().thrown(this, this.getOwner()), SWORD_DAMAGE);
        }
    }

    /** 铁镐：给目标四件已装备护甲各来一发耐久损伤（10~20 点） */
    private void damageArmor(Entity target) {
        if (!(target instanceof LivingEntity living)) {
            return;
        }
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            ItemStack armor = living.getItemBySlot(slot);
            if (armor.isEmpty() || !armor.isDamageableItem()) {
                continue;
            }
            int amount = PICKAXE_DURABILITY_MIN
                    + living.getRandom().nextInt(PICKAXE_DURABILITY_MAX - PICKAXE_DURABILITY_MIN + 1);
            armor.hurtAndBreak(amount, living, slot);
        }
    }

    /** 铁斧：目标举着盾（副手盾牌且在格挡）就把盾打回冷却里 */
    private void breakShield(Entity target) {
        if (!(target instanceof LivingEntity living)) {
            return;
        }
        if (!living.isBlocking() || !living.getOffhandItem().is(Items.SHIELD)) {
            return;
        }
        if (living instanceof Player player) {
            player.getCooldowns().addCooldown(Items.SHIELD, SHIELD_COOLDOWN_TICKS);
        }
        this.level().playSound(null, living.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.NEUTRAL,
                1.0F, 0.8F + this.random.nextFloat() * 0.3F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ToolVariant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("ToolVariant"));
    }
}
