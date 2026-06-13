package net.mcreator.mut.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import net.mcreator.mut.block.entity.SigilForgeBossTrialSpawnerBlockEntity;

public enum BossSpawnerInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("bossName")) {
            tooltip.add(Component.literal("§cBoss: §f" + data.getString("bossName")));
            tooltip.add(Component.literal("§eState: §f" + data.getString("state")));
        }
        if (data.contains("cooldown") && data.getInt("cooldown") > 0) {
            int seconds = data.getInt("cooldown") / 20;
            tooltip.add(Component.literal("§7cooldown: §f" + seconds + "秒"));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof SigilForgeBossTrialSpawnerBlockEntity spawner) {
            tag.putString("bossName", spawner.getBossType().getDescription().getString());
            tag.putString("state", spawner.getBossState().getSerializedName());
            tag.putInt("cooldown", spawner.getCooldown());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("mut", "boss_spawner_info");
    }
}