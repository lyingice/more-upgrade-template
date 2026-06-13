package net.mcreator.mut.block.entity.trial_spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class TrialSpawnerLogic {

    private final TrialSpawnerConfig config;
    private TrialSpawnerState state = TrialSpawnerState.INACTIVE;
    private final Set<UUID> spawnedMobs = new HashSet<>();
    private int cooldownTimer;
    private int ejectTimer;
    private final Set<UUID> rewardedPlayers = new LinkedHashSet<>();
    private int currentWave = 0;
    private int waveTimer = 0;
    private boolean betweenWaves = false;
    private int waves = 3;
    private int waveInterval = 200;
    private int mobsPerWave = 3;
    private int mobsPerWavePerPlayer = 1;

    public int getWaves() { return waves; }
    public int getWaveInterval() { return waveInterval; }
    public int getMobsPerWave() { return mobsPerWave; }
    public int getMobsPerWavePerPlayer() { return mobsPerWavePerPlayer; }
    public int getCooldown() {
        return cooldownTimer;
    }

    public int getTotalMobsPerWave(int playerCount) {
        return mobsPerWave + (playerCount - 1) * mobsPerWavePerPlayer;
    }

    public TrialSpawnerLogic(TrialSpawnerConfig config) {
        this.config = config;
    }

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state, Runnable onChanged) {
        switch (this.state) {
            case INACTIVE -> tickInactive(level, pos, onChanged);
            case ACTIVE -> tickActive(level, pos, onChanged);
            case EJECTING -> tickEjecting(level, pos, onChanged);
            case COOLDOWN -> tickCooldown(level, pos, onChanged);
        }
    }

    private void tickInactive(ServerLevel level, BlockPos pos, Runnable onChanged) {
        boolean playerNearby = level.players().stream()
                .anyMatch(p -> p.blockPosition().distSqr(pos) <= config.getActivationRange() * config.getActivationRange()
                        && !p.isCreative() && !p.isSpectator());
        if (playerNearby) {
            setState(TrialSpawnerState.ACTIVE, onChanged);
            currentWave = 0;          // 重置波次
            betweenWaves = false;
            spawnWave(level, pos);    // ← 改成 spawnWave
            level.playSound(null, pos, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS);
        }
    }
    private void spawnWave(ServerLevel level, BlockPos pos) {
        EntityType<?> type = config.getMobType();
        int playerCount = Math.max(1, (int) level.players().stream()
                .filter(p -> p.blockPosition().distSqr(pos) <= config.getActivationRange() * config.getActivationRange())
                .filter(p -> !p.isCreative() && !p.isSpectator())
                .count());
        int totalMobs = config.getTotalMobsPerWave(playerCount);

        for (int i = 0; i < totalMobs; i++) {
            BlockPos spawnPos = pos.offset(
                    level.random.nextInt(7) - 3, 1, level.random.nextInt(7) - 3);
            Entity entity = type.spawn(level, spawnPos, MobSpawnType.TRIAL_SPAWNER);
            if (entity != null) spawnedMobs.add(entity.getUUID());
        }
        currentWave++;
        betweenWaves = false;
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS);
    }

    private void tickActive(ServerLevel level, BlockPos pos, Runnable onChanged) {
        spawnedMobs.removeIf(uuid -> level.getEntity(uuid) == null || !level.getEntity(uuid).isAlive());

        if (betweenWaves) {
            waveTimer--;
            if (waveTimer <= 0) {
                if (currentWave < config.getWaves()) {
                    spawnWave(level, pos);
                }
            }
            return;
        }

        if (spawnedMobs.isEmpty()) {
            if (currentWave < config.getWaves()) {
                // 还有下一波
                betweenWaves = true;
                waveTimer = config.getWaveInterval();
            } else {
                // 所有波次完成
                ejectTimer = config.getEjectDelay();
                setState(TrialSpawnerState.EJECTING, onChanged);
            }
        }

        boolean anyPlayer = level.players().stream()
                .anyMatch(p -> p.blockPosition().distSqr(pos) <= config.getActivationRange() * 2 * config.getActivationRange() * 2);
        if (!anyPlayer) {
            clearMobs(level);
            currentWave = 0;
            betweenWaves = false;
            setState(TrialSpawnerState.INACTIVE, onChanged);
        }
    }

    private void tickEjecting(ServerLevel level, BlockPos pos, Runnable onChanged) {
        ejectTimer--;
        if (ejectTimer <= 0) {
            ejectRewards(level, pos);
            cooldownTimer = config.getCooldownTime();
            setState(TrialSpawnerState.COOLDOWN, onChanged);
        }
    }

    private void tickCooldown(ServerLevel level, BlockPos pos, Runnable onChanged) {
        cooldownTimer--;
        if (cooldownTimer <= 0) {
            setState(TrialSpawnerState.INACTIVE, onChanged);
        }
    }


    private void clearMobs(ServerLevel level) {
        for (UUID uuid : spawnedMobs) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) entity.discard();
        }
        spawnedMobs.clear();
    }

/**
 * 向指定位置喷射奖励物品
 * @param level 服务器世界实例
 * @param pos 坐标位置
 */
    private void ejectRewards(ServerLevel level, BlockPos pos) {
    // 获取配置中的奖励物品并创建副本
        ItemStack reward = config.getRewardItem().copy();
    // 获取配置中的奖励数量
        int count = config.getRewardCount();
    // 如果奖励物品不为空
        if (!reward.isEmpty()) {
        // 循环生成指定数量的奖励物品
            for (int i = 0; i < count; i++) {
            // 生成奖励物品副本，向上喷射1.2格，水平扩散2格
                DefaultDispenseItemBehavior.spawnItem(level, reward.copy(), 2, Direction.UP,
                        Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2));
            }
        }
    // 播放物品喷射音效
        level.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS);
    // 播放试验生成器物品喷射音效
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS);
    // 触发粒子效果事件
        level.levelEvent(3017, pos, 0);
    }

    public boolean trySetMobType(Player player, ItemStack stack) {
        if (player.isCreative() && stack.getItem() instanceof net.minecraft.world.item.SpawnEggItem spawnEgg) {
            config.setMobType(spawnEgg.getType(stack));
            return true;
        }
        return false;
    }

    public EntityType<?> getMobType() { return config.getMobType(); }
    public TrialSpawnerState getState() { return state; }
    public TrialSpawnerConfig getConfig() { return config; }

    public void setState(TrialSpawnerState newState, Runnable onChanged) {
        this.state = newState;
        onChanged.run();
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("state", state.getSerializedName());
        tag.put("config", config.save(registries));
        tag.putInt("cooldownTimer", cooldownTimer);
        tag.putInt("ejectTimer", ejectTimer);
        tag.putInt("currentWave", currentWave);
        tag.putInt("waveTimer", waveTimer);
        tag.putBoolean("betweenWaves", betweenWaves);
        CompoundTag mobs = new CompoundTag();
        int i = 0;
        for (UUID uuid : spawnedMobs) {
            mobs.putUUID("m" + i, uuid);
            i++;
        }
        mobs.putInt("size", i);
        tag.put("spawnedMobs", mobs);
        CompoundTag rewarded = new CompoundTag();
        i = 0;
        for (UUID uuid : rewardedPlayers) {
            rewarded.putUUID("r" + i, uuid);
            i++;
        }
        rewarded.putInt("size", i);
        tag.put("rewardedPlayers", rewarded);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("state")) {
            state = TrialSpawnerState.valueOf(tag.getString("state").toUpperCase());
        }
        if (tag.contains("config")) config.load(tag.getCompound("config"), registries);
        cooldownTimer = tag.getInt("cooldownTimer");
        ejectTimer = tag.getInt("ejectTimer");
        currentWave = tag.getInt("currentWave");
        waveTimer = tag.getInt("waveTimer");
        betweenWaves = tag.getBoolean("betweenWaves");
        spawnedMobs.clear();
        if (tag.contains("spawnedMobs")) {
            CompoundTag mobs = tag.getCompound("spawnedMobs");
            int size = mobs.getInt("size");
            for (int i = 0; i < size; i++) {
                spawnedMobs.add(mobs.getUUID("m" + i));
            }
        }
        rewardedPlayers.clear();
        if (tag.contains("rewardedPlayers")) {
            CompoundTag rewarded = tag.getCompound("rewardedPlayers");
            int size = rewarded.getInt("size");
            for (int i = 0; i < size; i++) {
                rewardedPlayers.add(rewarded.getUUID("r" + i));
            }
        }
    }
}