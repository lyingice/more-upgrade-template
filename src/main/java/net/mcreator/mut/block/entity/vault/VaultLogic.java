package net.mcreator.mut.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class VaultLogic {

    private final VaultConfig config;
    private final VaultPlayerTracker playerTracker;

    private List<ItemStack> cachedRewards = new ArrayList<>();
    private ItemStack displayItem = ItemStack.EMPTY;
    private long stateUpdatePauseUntil;
    private long lastFailSoundTime;

    public VaultLogic(VaultConfig config) {
        this.config = config;
        this.playerTracker = new VaultPlayerTracker();
    }

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state, Runnable onChanged) {
        VaultState vaultState = state.getValue(VaultBlock.STATE);
        long gameTime = level.getGameTime();

        if (vaultState == VaultState.EJECTING) {
            if (gameTime >= stateUpdatePauseUntil) {
                if (!cachedRewards.isEmpty()) {
                    ItemStack reward = cachedRewards.remove(cachedRewards.size() - 1);
                    ejectItem(level, pos, reward);
                    displayItem = cachedRewards.isEmpty() ? ItemStack.EMPTY : cachedRewards.get(cachedRewards.size() - 1).copy();
                    stateUpdatePauseUntil = gameTime + 20;
                    onChanged.run();
                } else {
                    displayItem = ItemStack.EMPTY;
                    setState(level, pos, state, getTargetState(level, pos), onChanged);
                }
            }
            return;
        }

        if (vaultState == VaultState.UNLOCKING && gameTime >= stateUpdatePauseUntil) {
            setState(level, pos, state, VaultState.EJECTING, onChanged);
            level.playSound(null, pos, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS);
            stateUpdatePauseUntil = gameTime + 20;
            return;
        }

        if (gameTime < stateUpdatePauseUntil) return;

        if (gameTime % 20 == 0 && vaultState == VaultState.ACTIVE) {
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(config.lootTable());
            displayItem = VaultLoot.getRandomDisplayItem(level, lootTable, pos);
            onChanged.run();
        }

        boolean hasPlayers = !playerTracker.getNearbyPlayers(level, pos, config.activationRange()).isEmpty();

        if (vaultState == VaultState.INACTIVE && hasPlayers) {
            setState(level, pos, state, VaultState.ACTIVE, onChanged);
            stateUpdatePauseUntil = gameTime + 20;
        } else if (vaultState == VaultState.ACTIVE && !hasPlayers) {
            setState(level, pos, state, VaultState.INACTIVE, onChanged);
            stateUpdatePauseUntil = gameTime + 20;
        }
    }

    public boolean tryInsertKey(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        VaultState vaultState = state.getValue(VaultBlock.STATE);
        if (vaultState != VaultState.ACTIVE) return false;

        if (!ItemStack.isSameItemSameComponents(stack, config.keyItem())) {
            playFailSound(level, pos, SoundEvents.VAULT_INSERT_ITEM_FAIL);
            return false;
        }

        if (playerTracker.hasRewarded(player)) {
            playFailSound(level, pos, SoundEvents.VAULT_REJECT_REWARDED_PLAYER);
            return false;
        }

        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(config.lootTable());
        List<ItemStack> rewards = VaultLoot.resolveLoot(level, lootTable, pos, player);
        if (rewards.isEmpty()) return false;

        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        stack.consume(config.keyItem().getCount(), player);
        playerTracker.markRewarded(player);

        cachedRewards = new ArrayList<>(rewards);
        displayItem = rewards.get(rewards.size() - 1).copy();

        level.playSound(null, pos, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS);
        setState(level, pos, state, VaultState.UNLOCKING, () -> {});
        stateUpdatePauseUntil = level.getGameTime() + 14;
        return true;
    }

    public boolean hasDisplayItem() { return !displayItem.isEmpty(); }
    public ItemStack getDisplayItem() { return displayItem; }
    public VaultPlayerTracker getPlayerTracker() { return playerTracker; }
    public void setDisplayItem(ItemStack item) { this.displayItem = item; }

    private VaultState getTargetState(ServerLevel level, BlockPos pos) {
        return playerTracker.getNearbyPlayers(level, pos, config.deactivationRange()).isEmpty()
                ? VaultState.INACTIVE : VaultState.ACTIVE;
    }

    private void setState(ServerLevel level, BlockPos pos, BlockState oldState, VaultState newState, Runnable onChanged) {
        if (oldState.getValue(VaultBlock.STATE) == newState) return;

        VaultState old = oldState.getValue(VaultBlock.STATE);
        level.setBlock(pos, oldState.setValue(VaultBlock.STATE, newState), 3);

        // 直接在服务端播放音效（所有附近玩家都能听到）
        if (newState == VaultState.ACTIVE && old == VaultState.INACTIVE) {
            level.playSound(null, pos, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if (newState == VaultState.INACTIVE && old == VaultState.ACTIVE) {
            level.playSound(null, pos, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        onChanged.run();
    }

    private void ejectItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        DefaultDispenseItemBehavior.spawnItem(level, stack, 2, Direction.UP,
                Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2));
        level.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * level.random.nextFloat());
        level.levelEvent(3017, pos, 0);
    }

    private void playFailSound(ServerLevel level, BlockPos pos, net.minecraft.sounds.SoundEvent sound) {
        long gameTime = level.getGameTime();
        if (gameTime >= lastFailSoundTime + 15) {
            level.playSound(null, pos, sound, SoundSource.BLOCKS);
            lastFailSoundTime = gameTime;
        }
    }
}