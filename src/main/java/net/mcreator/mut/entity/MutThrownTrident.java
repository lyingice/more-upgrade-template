package net.mcreator.mut.entity;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MutThrownTrident extends ThrownTrident {

    private static final EntityDataAccessor<ItemStack> CUSTOM_STACK =
            SynchedEntityData.defineId(MutThrownTrident.class, EntityDataSerializers.ITEM_STACK);

    public MutThrownTrident(EntityType<? extends MutThrownTrident> type, Level level) {
        super(type, level);
    }

    public MutThrownTrident(Level level, LivingEntity shooter, ItemStack stack) {
        super(level, shooter, stack);
        this.entityData.set(CUSTOM_STACK, stack.copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CUSTOM_STACK, ItemStack.EMPTY);
    }

    public ItemStack getCustomStack() {
        return this.entityData.get(CUSTOM_STACK);
    }

    @Override
    public EntityType<?> getType() {
        return net.mcreator.mut.init.MutModItems.MUT_THROWN_TRIDENT.get();
    }
    @Override
    protected void onHitEntity(net.minecraft.world.phys.EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = (float) this.getBaseDamage();
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, entity1 == null ? this : entity1);
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        // 通过反射设置 dealtDamage
        try {
            java.lang.reflect.Field field = ThrownTrident.class.getDeclaredField("dealtDamage");
            field.setAccessible(true);
            field.setBoolean(this, true);
        } catch (Exception ignored) {}

        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) return;
            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, entity, damagesource, this.getWeaponItem());
            }
            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }
}