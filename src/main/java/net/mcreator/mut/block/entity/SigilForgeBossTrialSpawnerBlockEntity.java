package net.mcreator.mut.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.mut.block.SigilForgeBossTrialSpawnerBlock;
import net.mcreator.mut.block.entity.boss_spawner.BossSpawnerConfig;
import net.mcreator.mut.block.entity.boss_spawner.BossSpawnerLogic;
import net.mcreator.mut.block.entity.boss_spawner.BossSpawnerState;
import net.mcreator.mut.init.MutModBlockEntities;

public class SigilForgeBossTrialSpawnerBlockEntity extends BlockEntity implements net.minecraft.world.Nameable{

    private final BossSpawnerConfig config = new BossSpawnerConfig();
    private final BossSpawnerLogic logic = new BossSpawnerLogic(config);
    private Entity cachedEntity;
    public int getCooldown() {
        return logic.getCooldown();
    }

    public SigilForgeBossTrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(MutModBlockEntities.SIGIL_FORGE_BOSS_TRIAL_SPAWNER.get(), pos, state);
    }

    // ===== Tick =====
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        logic.serverTick(level, pos, state, this::syncState);
    }

    // ===== 激活 =====
    public boolean tryActivate(ServerLevel level, BlockPos pos, Player player, ItemStack stack) {
        boolean result = logic.tryActivate(level, pos, player, stack);
        if (result) syncState();
        return result;
    }

    // ===== 设置Boss =====
    public void setBossType(EntityType<?> type) {
        logic.setBossType(type);
        setChanged();
    }

    public EntityType<?> getBossType() { return logic.getBossType(); }
    public BossSpawnerState getBossState() { return logic.getState(); }

    // ===== 同步状态到BlockState =====
    private void syncState() {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        BossSpawnerState newState = logic.getState();
        if (state.getValue(SigilForgeBossTrialSpawnerBlock.STATE) != newState) {
            level.setBlock(worldPosition, state.setValue(SigilForgeBossTrialSpawnerBlock.STATE, newState), 3);
        }
        setChanged();
    }

    // ===== 网络同步 =====
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("state", logic.getState().getSerializedName());
        tag.putString("bossType", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(logic.getBossType()).toString());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        logic.save(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        logic.load(tag, registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    @Override
    public Component getName() {
        return Component.literal("首领试炼刷怪笼");
    }

    @Override
    public Component getDisplayName() {
        EntityType<?> type = logic.getBossType();
        String bossName = type != null ? type.getDescription().getString() : "未设置";
        BossSpawnerState state = logic.getState();

        Component name = Component.literal("§6首领试炼刷怪笼 §7- §c" + bossName);
        Component status = Component.literal("§7状态: §e" + getStateText(state));

        return name.copy().append("\n").append(status);
    }

    private String getStateText(BossSpawnerState state) {
        return switch (state) {
            case INACTIVE -> "等待激活";
            case CHARGING -> "召唤中...";
            case ACTIVE -> "§c战斗中";
            case EJECTING -> "§a已击败";
            case COOLDOWN -> "冷却中";
        };
    }

    public CompoundTag getJadeData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("bossType", BuiltInRegistries.ENTITY_TYPE.getKey(logic.getBossType()).toString());
        tag.putString("state", logic.getState().getSerializedName());
        return tag;
    }
}