package net.mcreator.mut.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.vault.*;
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.mut.block.SigilForgeVaultCommonBlock;
import net.mcreator.mut.block.entity.vault.VaultEffects;
import net.mcreator.mut.block.entity.vault.VaultLogic;
import net.mcreator.mut.init.MutModBlockEntities;

import java.util.Optional;

public class SigilForgeVaultCommonBlockEntity extends BlockEntity {

    private VaultServerData serverData;
    private VaultSharedData sharedData;
    private VaultClientData clientData;
    protected VaultConfig config;

    private VaultLogic vaultLogic;
    private VaultEffects vaultEffects;

    public SigilForgeVaultCommonBlockEntity(BlockPos pos, BlockState state) {
        super(MutModBlockEntities.SIGIL_FORGE_VAULT_COMMON.get(), pos, state);
        init();
    }

    protected SigilForgeVaultCommonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        init();
    }

    private void init() {
        this.serverData = reflectNew(VaultServerData.class);
        this.sharedData = reflectNew(VaultSharedData.class);
        this.clientData = reflectNew(VaultClientData.class);
        this.config = createConfig();
        this.vaultLogic = new VaultLogic(config);
        this.vaultEffects = new VaultEffects();
    }

    protected VaultConfig createConfig() {
        return new VaultConfig(
                SigilForgeVaultCommonBlock.CUSTOM_LOOT_TABLE,
                4.0, 4.5,
                new ItemStack(Items.TRIAL_KEY),
                Optional.empty(),
                net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
                net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
        );
    }

    // ========== 委托给模块 ==========

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        vaultLogic.serverTick(level, pos, state, this::setChanged);
    }

    public void clientTick(Level level, BlockPos pos, BlockState state) {
        vaultEffects.clientTick(level, pos, state.getValue(VaultBlock.STATE), vaultLogic.hasDisplayItem());
    }

    public boolean tryInsertKey(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        boolean result = vaultLogic.tryInsertKey(level, pos, state, player, stack);
        if (result) setChanged();
        return result;
    }

    public float getDisplaySpin(float partialTicks) { return vaultEffects.getDisplaySpin(partialTicks); }
    public ItemStack getDisplayItem() { return vaultLogic.getDisplayItem(); }

    // ========== NBT ==========

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ItemStack display = vaultLogic.getDisplayItem();
        if (!display.isEmpty()) tag.put("displayItem", display.save(registries));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ItemStack display = vaultLogic.getDisplayItem();
        if (!display.isEmpty()) tag.put("displayItem", display.save(registries));
        vaultLogic.getPlayerTracker().save(tag);
    }
    // 在 SigilForgeVaultCommonBlockEntity 中添加
    public void onLevelEvent(int eventID, int eventParam) {
        if (level == null || !level.isClientSide) return;  // ← 改这里！只在客户端执行

        switch (eventID) {
            case 3015:
                level.playLocalSound(worldPosition, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                VaultEffects.emitActivationParticles(level, worldPosition,
                        eventParam == 1 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
                break;
            case 3016:
                level.playLocalSound(worldPosition, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                VaultEffects.emitDeactivationParticles(level, worldPosition,
                        eventParam == 1 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
                break;
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("displayItem")) {
            ItemStack.parse(registries, tag.getCompound("displayItem"))
                    .ifPresent(item -> vaultLogic.setDisplayItem(item));
        }
        vaultLogic.getPlayerTracker().load(tag);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T reflectNew(Class<T> clazz) {
        try {
            for (java.lang.reflect.Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 0) {
                    ctor.setAccessible(true);
                    return (T) ctor.newInstance();
                }
            }
            throw new RuntimeException("No no-arg constructor");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create " + clazz.getSimpleName(), e);
        }
    }
}