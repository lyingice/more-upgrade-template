package net.mcreator.mut.block.entity.trial_spawner;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrialSpawnerConfig {

    private EntityType<?> mobType = EntityType.ZOMBIE;
private int activationRange = 14;
private int mobCount = 0;
private int mobCountPerPlayer = 0;
private int cooldownTime = 18000;  // 15分钟
private int ejectDelay = 40;       // 弹出延迟2秒
protected ItemStack rewardItem = Items.TRIAL_KEY.getDefaultInstance();
private int rewardCount = 1;

public TrialSpawnerConfig() {}

public EntityType<?> getMobType() { return mobType; }
public int getActivationRange() { return activationRange; }
public int getMobCount() { return mobCount; }
public int getMobCountPerPlayer() { return mobCountPerPlayer; }
public int getCooldownTime() { return cooldownTime; }
public int getEjectDelay() { return ejectDelay; }
public ItemStack getRewardItem() { return rewardItem; }
public int getRewardCount() { return rewardCount; }

public void setMobType(EntityType<?> type) { this.mobType = type; }
public void setRewardItem(ItemStack reward) { this.rewardItem = reward; }
public void setRewardCount(int count) { this.rewardCount = count; }

public int getTotalMobCount(int playerCount) {
    return mobCount + (playerCount - 1) * mobCountPerPlayer;
}
private int waves = 3;                    // 波次数
private int waveInterval = 100;           // 波次间隔 10秒
private int mobsPerWave = 3;              // 每波怪物数
private int mobsPerWavePerPlayer = 1;     // 每多一个玩家增加的怪物数

public int getWaves() { return waves; }
public int getWaveInterval() { return waveInterval; }
public int getMobsPerWave() { return mobsPerWave; }
public int getMobsPerWavePerPlayer() { return mobsPerWavePerPlayer; }

public int getTotalMobsPerWave(int playerCount) {
    return mobsPerWave + (playerCount - 1) * mobsPerWavePerPlayer;
}
public CompoundTag save(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    tag.putString("mobType", BuiltInRegistries.ENTITY_TYPE.getKey(mobType).toString());
    tag.putInt("activationRange", activationRange);
    tag.putInt("mobCount", mobCount);
    tag.putInt("mobCountPerPlayer", mobCountPerPlayer);
    tag.putInt("cooldownTime", cooldownTime);
    tag.putInt("ejectDelay", ejectDelay);
    tag.putInt("rewardCount", rewardCount);
    if (!rewardItem.isEmpty()) tag.put("rewardItem", rewardItem.save(registries));
    return tag;
}

public void load(CompoundTag tag, HolderLookup.Provider registries) {
    if (tag.contains("mobType")) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("mobType"));
        if (id != null) mobType = BuiltInRegistries.ENTITY_TYPE.get(id);
    }
    if (tag.contains("activationRange")) activationRange = tag.getInt("activationRange");
    if (tag.contains("mobCount")) mobCount = tag.getInt("mobCount");
    if (tag.contains("mobCountPerPlayer")) mobCountPerPlayer = tag.getInt("mobCountPerPlayer");
    if (tag.contains("cooldownTime")) cooldownTime = tag.getInt("cooldownTime");
    if (tag.contains("ejectDelay")) ejectDelay = tag.getInt("ejectDelay");
    if (tag.contains("rewardCount")) rewardCount = tag.getInt("rewardCount");
    if (tag.contains("rewardItem")) {
        rewardItem = ItemStack.parse(registries, tag.getCompound("rewardItem")).orElse(Items.EMERALD.getDefaultInstance());
    }
}}
