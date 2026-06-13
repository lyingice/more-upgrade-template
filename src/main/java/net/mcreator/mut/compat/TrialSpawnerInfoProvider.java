package net.mcreator.mut.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import net.mcreator.mut.block.entity.SigilForgeTrialSpawnerBlockEntity;

public enum TrialSpawnerInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("mobName")) {
            tooltip.add(Component.literal("§cMob: §f" + data.getString("mobName")));
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
        if (be instanceof SigilForgeTrialSpawnerBlockEntity spawner) {
            tag.putString("mobName", spawner.getMobType().getDescription().getString());
            tag.putString("state", spawner.getSpawnerState().getSerializedName());
            tag.putInt("cooldown", spawner.getCooldown());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("mut", "trial_spawner_info");
    }
}