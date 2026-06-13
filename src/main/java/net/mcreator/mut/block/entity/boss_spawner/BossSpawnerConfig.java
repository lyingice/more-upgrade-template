package net.mcreator.mut.block.entity.boss_spawner;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class BossSpawnerConfig {

    private EntityType<?> bossType = EntityType.ZOMBIE;
    private int chargeTime = 60;          // 3秒
    private int bossCheckRange = 64;
    private int bossDespawnTime = 100;    // 5秒
    private int cooldownTime = 6000;      // 5分钟
    private ItemStack keyItem = new ItemStack(MutModItems.SIGIL_FORGE_BOSS_KEY.get());
    private ItemStack rewardItem = new ItemStack(MutModItems.BOSS_TRIAL_KEY.get());

    public BossSpawnerConfig() {}

    public EntityType<?> getBossType() { return bossType; }
    public int getChargeTime() { return chargeTime; }
    public int getBossCheckRange() { return bossCheckRange; }
    public int getBossDespawnTime() { return bossDespawnTime; }
    public int getCooldownTime() { return cooldownTime; }
    public ItemStack getKeyItem() { return keyItem; }
    public ItemStack getRewardItem() { return rewardItem; }

    public void setBossType(EntityType<?> type) { this.bossType = type; }
    public void setKeyItem(ItemStack key) { this.keyItem = key; }
    public void setRewardItem(ItemStack reward) { this.rewardItem = reward; }

    // NBT 序列化
    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("bossType", BuiltInRegistries.ENTITY_TYPE.getKey(bossType).toString());
        tag.putInt("chargeTime", chargeTime);
        tag.putInt("bossCheckRange", bossCheckRange);
        tag.putInt("bossDespawnTime", bossDespawnTime);
        tag.putInt("cooldownTime", cooldownTime);
        if (!keyItem.isEmpty()) tag.put("keyItem", keyItem.save(registries));
        if (!rewardItem.isEmpty()) tag.put("rewardItem", rewardItem.save(registries));
        return tag;
    }

    // NBT 反序列化
    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("bossType")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("bossType"));
            if (id != null) bossType = BuiltInRegistries.ENTITY_TYPE.get(id);
        }
        if (tag.contains("chargeTime")) chargeTime = tag.getInt("chargeTime");
        if (tag.contains("bossCheckRange")) bossCheckRange = tag.getInt("bossCheckRange");
        if (tag.contains("bossDespawnTime")) bossDespawnTime = tag.getInt("bossDespawnTime");
        if (tag.contains("cooldownTime")) cooldownTime = tag.getInt("cooldownTime");
        if (tag.contains("keyItem")) {
            keyItem = ItemStack.parse(registries, tag.getCompound("keyItem")).orElse(ItemStack.EMPTY);
        }
        if (tag.contains("rewardItem")) {
            rewardItem = ItemStack.parse(registries, tag.getCompound("rewardItem")).orElse(ItemStack.EMPTY);
        }
    }
}