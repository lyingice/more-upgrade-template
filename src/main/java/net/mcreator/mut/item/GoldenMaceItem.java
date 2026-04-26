package net.mcreator.mut.item;

import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

public class GoldenMaceItem extends MaceItem {
    
    private static final float DAMAGE_MULTIPLIER = 0.6f;   // 下落伤害倍率0.6（与木锤一致）
    private static final int DEFAULT_ATTACK_DAMAGE = 3;    // 基础伤害4 (3+1)
    private static final float DEFAULT_ATTACK_SPEED = -3.4F;
    
    public GoldenMaceItem() {
        super(new Item.Properties()
            .durability(59)  // 金工具耐久59（与木工具一致）
            .attributes(createAttributes())
        );
    }
    
    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE, 
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)DEFAULT_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE), 
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED, 
                new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)DEFAULT_ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), 
                EquipmentSlotGroup.MAINHAND)
            .build();
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayer serverplayer) {
            if (canSmashAttack(serverplayer)) {
                ServerLevel serverlevel = (ServerLevel) attacker.level();
                
                if (serverplayer.isIgnoringFallDamageFromCurrentImpulse() && serverplayer.currentImpulseImpactPos != null) {
                    if (serverplayer.currentImpulseImpactPos.y > serverplayer.position().y) {
                        serverplayer.currentImpulseImpactPos = serverplayer.position();
                    }
                } else {
                    serverplayer.currentImpulseImpactPos = serverplayer.position();
                }
                
                serverplayer.setIgnoreFallDamageFromCurrentImpulse(true);
                serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().with(Direction.Axis.Y, 0.1F));
                serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
                
                if (target.onGround()) {
                    serverplayer.setSpawnExtraParticlesOnFall(true);
                    serverlevel.playSound(null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), 
                        serverplayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND, 
                        serverplayer.getSoundSource(), 1.0F, 1.0F);
                } else {
                    serverlevel.playSound(null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), 
                        SoundEvents.MACE_SMASH_AIR, serverplayer.getSoundSource(), 1.0F, 1.0F);
                }
                
                knockback(serverlevel, serverplayer, target);
            }
        }
        return true;
    }
    
    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (canSmashAttack(attacker)) {
            attacker.resetFallDistance();
        }
    }
    
    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource source) {
        Entity directEntity = source.getDirectEntity();
        if (directEntity instanceof LivingEntity livingentity) {
            if (!canSmashAttack(livingentity)) {
                return 0.0F;
            } else {
                float fallDistance = livingentity.fallDistance;
                float baseDamage;
                if (fallDistance <= 3.0F) {
                    baseDamage = 4.0F * fallDistance;
                } else if (fallDistance <= 8.0F) {
                    baseDamage = 12.0F + 2.0F * (fallDistance - 3.0F);
                } else {
                    baseDamage = 22.0F + fallDistance - 8.0F;
                }
                
                // 应用0.6倍率（与木锤一致）
                baseDamage = baseDamage * DAMAGE_MULTIPLIER;
                
                if (livingentity.level() instanceof ServerLevel serverlevel) {
                    baseDamage = baseDamage + EnchantmentHelper.modifyFallBasedDamage(serverlevel, 
                        livingentity.getWeaponItem(), target, source, 0.0F) * fallDistance;
                }
                
                return baseDamage;
            }
        }
        return 0.0F;
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.GOLD_INGOT);  // 金锭修复
    }
    
    @Override
    public int getEnchantmentValue() {
        return 22;  // 金工具附魔能力22（最高）
    }
    
    private static void knockback(ServerLevel level, Player player, Entity target) {
        // 粒子效果
        level.levelEvent(2013, target.getOnPos(), 750);
        
        double radius = 3.5;
        level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(radius), 
            e -> {
                return !e.isSpectator() 
                    && e != player 
                    && e != target 
                    && !player.isAlliedTo(e)
                    && target.distanceToSqr(e) <= Math.pow(radius, 2);
            }).forEach(entity -> {
            Vec3 vec3 = entity.position().subtract(target.position());
            double distance = vec3.length();
            if (distance < radius) {
                double power = (radius - distance) * 0.7 * (player.fallDistance > 5.0F ? 2 : 1);
                double knockbackResistance = entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                power = power * (1.0 - knockbackResistance);
                Vec3 knockbackVec = vec3.normalize().scale(power);
                entity.push(knockbackVec.x, 0.7, knockbackVec.z);
                if (entity instanceof ServerPlayer serverplayer) {
                    serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
                }
            }
        });
    }
}