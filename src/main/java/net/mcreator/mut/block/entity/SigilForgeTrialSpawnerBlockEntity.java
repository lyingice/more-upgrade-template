package net.mcreator.mut.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.mcreator.mut.block.SigilForgeTrialSpawnerBlock;
import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerConfig;
import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerLogic;
import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerState;
import net.mcreator.mut.init.MutModBlockEntities;

public class SigilForgeTrialSpawnerBlockEntity extends BlockEntity {

    protected final TrialSpawnerLogic logic;

    public SigilForgeTrialSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TrialSpawnerConfig config) {
        super(type, pos, state);
        this.logic = new TrialSpawnerLogic(config);
    }

    public SigilForgeTrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        this(MutModBlockEntities.SIGIL_FORGE_TRIAL_SPAWNER.get(), pos, state, new TrialSpawnerConfig());
    }


    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        logic.serverTick(level, pos, state, this::syncState);
    }

    public boolean trySetMobType(Player player, ItemStack stack) {
        boolean result = logic.trySetMobType(player, stack);
        if (result) setChanged();
        return result;
    }

    public EntityType<?> getMobType() { return logic.getMobType(); }
    public TrialSpawnerState getSpawnerState() { return logic.getState(); }

    private void syncState() {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        TrialSpawnerState newState = logic.getState();
        if (state.getValue(SigilForgeTrialSpawnerBlock.STATE) != newState) {
            level.setBlock(worldPosition, state.setValue(SigilForgeTrialSpawnerBlock.STATE, newState), 3);
        }
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("state", logic.getState().getSerializedName());
        tag.putString("mobType", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(logic.getMobType()).toString());
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
    public int getCooldown() {
        return logic.getCooldown();
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
}