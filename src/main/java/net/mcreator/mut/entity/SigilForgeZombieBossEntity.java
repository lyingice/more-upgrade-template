package net.mcreator.mut.entity;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.init.MutModEntities;
import net.mcreator.mut.init.MutModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;

import java.util.List;
@EventBusSubscriber(modid = MutMod.MODID)
public class SigilForgeZombieBossEntity extends EliteAborigineZombieEntity{
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(MutModEntities.SIGIL_FORGE_ZOMBIE_BOSS.get(), createAttributes().build());
    }
    // 召唤冷却
    private int summonCooldown = 0;
    private static final int SUMMON_COOLDOWN_MIN = 300; // 15秒
    private static final int SUMMON_COOLDOWN_MAX = 600; // 30秒
    private boolean bossBarAdded = false;
    private boolean childEquipmentInitialized = false;

    // 被攻击召唤概率
    private static final float HURT_SUMMON_CHANCE = 0.15F; // 15%

    public SigilForgeZombieBossEntity(EntityType<? extends EliteAborigineZombieEntity> type, Level level) {
        super((EntityType<EliteAborigineZombieEntity>) type, level);
        summonCooldown = 200;
        this.setPersistenceRequired();
        this.bossEvent = new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.PROGRESS);
    }
    private final ServerBossEvent bossEvent;
    public ServerBossEvent getBossEvent() {
        return bossEvent;
    }
    public boolean shouldShowBossBar() {
        return true;
    }
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!this.level().isClientSide()) {
            this.bossEvent.addPlayer(player);  // ← 关键：添加玩家到血条列表
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (!this.level().isClientSide()) {
            this.bossEvent.removePlayer(player);  // ← 移除玩家
        }
    }
    private void initEquipment() {
        // 创建纹饰
        ArmorTrim trim = new ArmorTrim(
                this.level().registryAccess()
                        .registryOrThrow(Registries.TRIM_MATERIAL)
                        .getHolderOrThrow(TrimMaterials.REDSTONE),
                this.level().registryAccess()
                        .registryOrThrow(Registries.TRIM_PATTERN)
                        .getHolderOrThrow(TrimPatterns.SILENCE)
        );

        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));

        // 应用纹饰到所有盔甲
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack armor = this.getItemBySlot(slot);
            if (!armor.isEmpty()) {
                armor.set(DataComponents.TRIM, trim);
            }
        }

        // 添加随机词缀
        addRandomAffixToEquipment();
    }
    private void addRandomAffixToEquipment() {
        EquipmentSlot[] armorSlots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = this.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            List<Affix> allAffixes = List.copyOf(AffixRegistry.getAll());
            if (allAffixes.isEmpty()) continue;

            // 随机选一个词缀
            Affix chosen = allAffixes.get(this.random.nextInt(allAffixes.size()));

            // 随机 4-6 级，不超过词缀最大等级
            int level = this.random.nextIntBetweenInclusive(4, 6);
            level = Math.min(level, chosen.getMaxLevel());

            // 应用
            chosen.applyToStack(stack, level);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 30.0)
                .add(Attributes.ARMOR_TOUGHNESS, 999.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }
    private int levitationTick = 0;
    private static final int MAX_LEVITATION_TICK = 120;
    private int sonicCooldown = 0;
    private static final int SONIC_COOLDOWN_MAX = 80;  // 4秒冷却
    private static final float MIN_SONIC_RANGE = 15F;   // 最小触发距离
    private static final float SONIC_DAMAGE = 40F;

    // ========== 获取难度相关参数 ==========
    private int getMaxMinions() {
        return switch (this.level().getDifficulty()) {
            case EASY -> 4;
            case NORMAL -> 8;
            case HARD -> 12; // 困难+4 = 8+4
            default -> 2;
        };
    }

    private int getSpawnCount() {
        return switch (this.level().getDifficulty()) {
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD -> 4; // 困难+2 = 2+2
            default -> 1;
        };
    }

    // ========== 统计周围小兵数量 ==========
    private int countNearbyMinions() {
        List<EliteAborigineZombieEntity> minions = this.level()
                .getEntitiesOfClass(EliteAborigineZombieEntity.class,
                        this.getBoundingBox().inflate(32),
                        e -> e != this && e.isAlive());
        return minions.size();
    }

    // ========== 召唤小兵 ==========
    private void summonMinions(int count) {
        if (this.level() instanceof ServerLevel sl) {
            for (int i = 0; i < count; i++) {
                EliteAborigineZombieEntity minion = MutModEntities.ELITE_ABORIGINE_ZOMBIE.get()
                        .create(sl);
                if (minion == null) continue;

                // 随机位置（Boss周围3-5格）
                double angle = this.random.nextDouble() * Math.PI * 2;
                double dist = 3 + this.random.nextDouble() * 2;
                double spawnX = this.getX() + Math.cos(angle) * dist;
                double spawnZ = this.getZ() + Math.sin(angle) * dist;
                double spawnY = this.getY();

                minion.setPos(spawnX, spawnY, spawnZ);
                minion.setTarget(this.getTarget()); // 继承Boss的仇恨目标
                sl.addFreshEntity(minion);

                // 召唤粒子效果
                sl.sendParticles(MutModParticles.GRAY_SONIC_BOOM.get(),
                        spawnX, spawnY + 1, spawnZ,
                        5, 0.5, 0.5, 0.5, 0);
            }

            this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.5F);
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !childEquipmentInitialized && this.isAlive()) {
            childEquipmentInitialized = true;
            initEquipment();
        }
        if (!this.level().isClientSide()) {
            // 首次 tick 时添加 Boss 血条
            if (!bossBarAdded) {
                this.bossEvent.setVisible(true);
                bossBarAdded = true;
            }
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.bossEvent.setName(this.getDisplayName());
        }

        if (this.level().isClientSide()) return;

        // === 声波攻击 ===
        if (sonicCooldown > 0) {
            sonicCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && sonicCooldown <= 0) {
            double dist = this.distanceToSqr(target);
            if (dist > MIN_SONIC_RANGE * MIN_SONIC_RANGE) {
                performSonicBoom(target);
                sonicCooldown = SONIC_COOLDOWN_MAX;
            }
        }

        // === 定时召唤 ===
        if (summonCooldown > 0) {
            summonCooldown--;
        }
        if (summonCooldown <= 0) {
            int maxMinions = getMaxMinions();
            int currentMinions = countNearbyMinions();
            if (currentMinions < maxMinions) {
                int toSpawn = Math.min(getSpawnCount(), maxMinions - currentMinions);
                summonMinions(toSpawn);
            }
            summonCooldown = SUMMON_COOLDOWN_MIN + this.random.nextInt(SUMMON_COOLDOWN_MAX - SUMMON_COOLDOWN_MIN + 1);
        }

        // === 漂浮逻辑 ===
        boolean inLava = this.level().getBlockState(this.blockPosition()).getFluidState().is(FluidTags.LAVA);
        for (BlockPos checkPos : BlockPos.betweenClosed(this.blockPosition().offset(-1, 0, -1), this.blockPosition().offset(1, 1, 1))) {
            if (this.level().getBlockState(checkPos).getFluidState().is(FluidTags.LAVA)) {
                inLava = true;
                break;
            }
        }
        if (inLava) {
            levitationTick = MAX_LEVITATION_TICK;
        }
        if (levitationTick > 0) {
            levitationTick--;
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.15, 0));
            this.fallDistance = 0;
        }
    }

    private void performSonicBoom(LivingEntity target) {
        Vec3 from = this.position().add(0, 1.8, 0);  // Boss胸口高度
        Vec3 to = target.position().add(0, target.getBbHeight() * 0.6, 0);
        Vec3 dir = to.subtract(from).normalize();

        // 播放音效
        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

        // 生成灰白声波粒子
        if (this.level() instanceof ServerLevel sl) {
            for (int i = 1; i <= (int) (from.distanceTo(to)); i++) {
                Vec3 point = from.add(dir.scale(i));
                sl.sendParticles(
                        MutModParticles.GRAY_SONIC_BOOM.get(),
                        point.x, point.y, point.z,
                        1, 0, 0, 0, 0
                );
            }
        }

        // 造成穿透伤害
        target.invulnerableTime = 0;
        target.hurt(this.damageSources().sonicBoom(this), SONIC_DAMAGE);
        target.invulnerableTime = 0;

        // 击退
        double knockback = 1.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        target.setDeltaMovement(target.getDeltaMovement().add(
                dir.x * knockback, 0.3, dir.z * knockback
        ));
        target.hurtMarked = true;
    }
    private int getFireAspectLevel(LivingEntity entity) {
        return entity.getMainHandItem().getEnchantmentLevel(
                this.level().registryAccess()
                        .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.FIRE_ASPECT));
    }

    private int getFlameLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        return stack.getEnchantmentLevel(
                this.level().registryAccess()
                        .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.FLAME));
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 直接火焰伤害
        if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) {
            boolean result = super.hurt(source, amount);
            if (result && !this.level().isClientSide()) {
                summonOnHurt();
            }
            return result;
        }

        // 远程火矢
        // 远程火矢
        if (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
            ItemStack weapon = arrow.getWeaponItem();
            boolean hasFlame = weapon != null && getFlameLevel(weapon) > 0;

            if (arrow.isOnFire() || hasFlame) {
                this.setRemainingFireTicks(100);
                boolean result = super.hurt(source, amount);
                if (result && !this.level().isClientSide()) {
                    summonOnHurt();
                }
                return result;
            }
        }

// 近战火焰附加
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            int fireAspectLevel = getFireAspectLevel(livingAttacker);
            if (fireAspectLevel > 0) {
                this.setRemainingFireTicks(fireAspectLevel * 80);
                boolean result = super.hurt(source, fireAspectLevel);
                if (result && !this.level().isClientSide()) {
                    summonOnHurt();
                }
                return result;
            }
        }

        return false;
    }
    private void summonOnHurt() {
        int maxMinions = getMaxMinions();
        int currentMinions = countNearbyMinions();
        if (currentMinions < maxMinions && this.random.nextFloat() < HURT_SUMMON_CHANCE) {
            int toSpawn = 1 + (currentMinions < maxMinions - 1 ? this.random.nextInt(2) : 0);
            toSpawn = Math.min(toSpawn, maxMinions - currentMinions);
            summonMinions(toSpawn);
        }
    }
    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }
    @Override
    public boolean isInWall() {
        return false;
    }
    @Override
    public void aiStep() {
        super.aiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossEvent.setName(this.getDisplayName());
        BlockPos pos = this.blockPosition();
        boolean inLava = this.level().getBlockState(pos).getFluidState().is(FluidTags.LAVA);

        // 检查周围是否有岩浆
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))) {
            if (this.level().getBlockState(checkPos).getFluidState().is(FluidTags.LAVA)) {
                inLava = true;
                break;
            }
        }

        if (inLava) {
            levitationTick = MAX_LEVITATION_TICK;
        }

        if (levitationTick > 0) {
            levitationTick--;
            // 漂浮效果：缓慢上升
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.15, 0));
            this.fallDistance = 0;
        }
    }
}
