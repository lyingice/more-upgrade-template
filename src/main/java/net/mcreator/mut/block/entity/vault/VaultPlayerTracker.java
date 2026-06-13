package net.mcreator.mut.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class VaultPlayerTracker {

    private final Set<UUID> rewardedPlayers = new LinkedHashSet<>();
    private static final int MAX_REWARDED_PLAYERS = 128;

    /**
     * 检测范围内的未领奖玩家
     */
    public Set<UUID> getNearbyPlayers(ServerLevel level, BlockPos pos, double range) {
        Set<UUID> players = new LinkedHashSet<>();
        level.players().stream()
                .filter(p -> p.blockPosition().distSqr(pos) <= range * range)
                .filter(p -> !hasRewarded(p))
                .forEach(p -> players.add(p.getUUID()));
        return players;
    }

    /**
     * 玩家是否已领过奖励
     */
    public boolean hasRewarded(Player player) {
        return rewardedPlayers.contains(player.getUUID());
    }

    /**
     * 标记玩家已领奖
     */
    public void markRewarded(Player player) {
        rewardedPlayers.add(player.getUUID());
        if (rewardedPlayers.size() > MAX_REWARDED_PLAYERS) {
            Iterator<UUID> it = rewardedPlayers.iterator();
            if (it.hasNext()) { it.next(); it.remove(); }
        }
    }

    /**
     * 获取所有已领奖玩家
     */
    public Set<UUID> getRewardedPlayers() {
        return rewardedPlayers;
    }

    /**
     * 序列化
     */
    public void save(CompoundTag tag) {
        if (!rewardedPlayers.isEmpty()) {
            CompoundTag rewardedTag = new CompoundTag();
            int i = 0;
            for (UUID uuid : rewardedPlayers) {
                rewardedTag.putUUID("p" + i, uuid);
                i++;
            }
            rewardedTag.putInt("size", i);
            tag.put("rewardedPlayers", rewardedTag);
        }
    }

    /**
     * 反序列化
     */
    public void load(CompoundTag tag) {
        rewardedPlayers.clear();
        if (tag.contains("rewardedPlayers")) {
            CompoundTag rewardedTag = tag.getCompound("rewardedPlayers");
            int size = rewardedTag.getInt("size");
            for (int i = 0; i < size; i++) {
                rewardedPlayers.add(rewardedTag.getUUID("p" + i));
            }
        }
    }
}