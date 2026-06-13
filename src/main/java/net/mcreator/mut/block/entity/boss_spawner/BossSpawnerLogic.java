package net.mcreator.mut.block.entity.boss_spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class BossSpawnerLogic {

    private final BossSpawnerConfig config;
    private BossSpawnerState state = BossSpawnerState.INACTIVE;
    private UUID bossUUID;
    private int chargeTimer;
    private int noPlayerTimer;
    private int cooldownTimer;
    private UUID activatorUUID;
    private int ejectTimer = 0;
    public int getCooldown() {
        return cooldownTimer;
    }


    public BossSpawnerLogic(BossSpawnerConfig config) {
        this.config = config;
    }

    // ===== 服务端Tick =====
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state, Runnable onChanged) {
        switch (this.state) {
            case INACTIVE -> {}
            case CHARGING -> tickCharging(level, pos, onChanged);
            case ACTIVE -> tickActive(level, pos, onChanged);
            case EJECTING -> tickEjecting(level, pos, onChanged);
            case COOLDOWN -> tickCooldown(level, pos, onChanged);
        }
    }

    // ===== 倒计时阶段 =====
    private void tickCharging(ServerLevel level, BlockPos pos, Runnable onChanged) {
        chargeTimer--;
        if (chargeTimer <= 0) {
            spawnBoss(level, pos);
            setState(BossSpawnerState.ACTIVE, onChanged);
        }
    }

    // ===== 战斗中 =====
    private void tickActive(ServerLevel level, BlockPos pos, Runnable onChanged) {
        Entity boss = getBoss(level);

        // Boss被击败
        if (boss == null || !boss.isAlive()) {
            setState(BossSpawnerState.EJECTING, onChanged);
            return;
        }

        // 检查附近是否有玩家
        boolean playerNearby = level.players().stream()
                .anyMatch(p -> p.distanceToSqr(boss) <= config.getBossCheckRange() * config.getBossCheckRange());

        if (!playerNearby) {
            noPlayerTimer++;
            if (noPlayerTimer >= config.getBossDespawnTime()) {
                // 玩家跑路，Boss消失，重置
                boss.discard();
                bossUUID = null;
                activatorUUID = null;
                setState(BossSpawnerState.INACTIVE, onChanged);
                level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS);
            }
        } else {
            noPlayerTimer = 0;
        }
    }

    // ===== 弹出奖励 =====
    private void tickEjecting(ServerLevel level, BlockPos pos, Runnable onChanged) {
        if (ejectTimer == 0) {
            // 第一帧：弹出物品
            ItemStack reward = config.getRewardItem().copy();
            if (!reward.isEmpty()) {
                DefaultDispenseItemBehavior.spawnItem(level, reward, 2, Direction.UP,
                        Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2));
            }
            level.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS);
            level.levelEvent(3017, pos, 0);
            ejectTimer = 60; // 保持 3 秒
        }

        ejectTimer--;
        if (ejectTimer <= 0) {
            bossUUID = null;
            activatorUUID = null;
            cooldownTimer = config.getCooldownTime();
            setState(BossSpawnerState.COOLDOWN, onChanged);
        }
    }

    // ===== 冷却 =====
    private void tickCooldown(ServerLevel level, BlockPos pos, Runnable onChanged) {
        cooldownTimer--;
        if (cooldownTimer <= 0) {
            setState(BossSpawnerState.INACTIVE, onChanged);
        }
    }

    // ===== 尝试激活 =====
    public boolean tryActivate(ServerLevel level, BlockPos pos, Player player, ItemStack stack) {
        if (state != BossSpawnerState.INACTIVE) return false;

        if (!ItemStack.isSameItemSameComponents(stack, config.getKeyItem())) {
            return false;
        }

        stack.consume(1, player);
        activatorUUID = player.getUUID();
        chargeTimer = config.getChargeTime();
        setState(BossSpawnerState.CHARGING, () -> {});

        level.playSound(null, pos, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS);
        return true;
    }

    // ===== 生成Boss =====
    private void spawnBoss(ServerLevel level, BlockPos pos) {
        EntityType<?> type = config.getBossType();
        Entity entity = type.spawn(level, pos.above(2), MobSpawnType.TRIAL_SPAWNER);
        if (entity instanceof LivingEntity living) {
            living.addTag("boss_trial_spawned");
            bossUUID = living.getUUID();
        }
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS);
    }

    // ===== 设置刷怪蛋 =====
    public void setBossType(EntityType<?> type) {
        config.setBossType(type);
    }

    public EntityType<?> getBossType() {
        return config.getBossType();
    }

    // ===== 获取Boss实体 =====
    private Entity getBoss(ServerLevel level) {
        if (bossUUID == null) return null;
        return level.getEntity(bossUUID);
    }

    // ===== 状态管理 =====
    public BossSpawnerState getState() { return state; }

    public void setState(BossSpawnerState newState, Runnable onChanged) {
        this.state = newState;
        onChanged.run();
    }

    // NBT
    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("state", state.getSerializedName());
        tag.put("config", config.save(registries));
        if (bossUUID != null) tag.putUUID("bossUUID", bossUUID);
        if (activatorUUID != null) tag.putUUID("activatorUUID", activatorUUID);
        tag.putInt("chargeTimer", chargeTimer);
        tag.putInt("noPlayerTimer", noPlayerTimer);
        tag.putInt("cooldownTimer", cooldownTimer);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("state")) {
            state = BossSpawnerState.valueOf(tag.getString("state").toUpperCase());
        }
        if (tag.contains("config")) config.load(tag.getCompound("config"), registries);
        if (tag.contains("bossUUID")) bossUUID = tag.getUUID("bossUUID");
        if (tag.contains("activatorUUID")) activatorUUID = tag.getUUID("activatorUUID");
        chargeTimer = tag.getInt("chargeTimer");
        noPlayerTimer = tag.getInt("noPlayerTimer");
        cooldownTimer = tag.getInt("cooldownTimer");
    }

    public BossSpawnerConfig getConfig() { return config; }
}