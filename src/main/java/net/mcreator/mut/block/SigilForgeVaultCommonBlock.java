package net.mcreator.mut.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.phys.BlockHitResult;

import net.mcreator.mut.block.entity.SigilForgeVaultCommonBlockEntity;
import net.mcreator.mut.init.MutModBlockEntities;

import javax.annotation.Nullable;

public class SigilForgeVaultCommonBlock extends VaultBlock {

    public static final ResourceKey<LootTable> CUSTOM_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("mut", "vault/common"));

    public SigilForgeVaultCommonBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.VAULT)
                .strength(50f)
                .lightLevel(s -> s.getValue(VaultBlock.STATE).lightLevel())
                .instrument(NoteBlockInstrument.BASEDRUM)
                .noOcclusion()                              // ← 已有
                .isViewBlocking((bs, br, bp) -> false)      // ← 加这个
                .isRedstoneConductor((bs, br, bp) -> false) // ← 已有
        );
    }

    protected SigilForgeVaultCommonBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SigilForgeVaultCommonBlockEntity(pos, state);
    }

    // 子类覆写这个来提供自己的 BlockEntityType
    protected BlockEntityType<?> getBlockEntityType() {
        return MutModBlockEntities.SIGIL_FORGE_VAULT_COMMON.get();
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                           BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(VaultBlock.STATE) != net.minecraft.world.level.block.entity.vault.VaultState.ACTIVE) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level instanceof ServerLevel serverLevel) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SigilForgeVaultCommonBlockEntity vaultEntity) {
                vaultEntity.tryInsertKey(serverLevel, pos, state, player, stack);
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.CONSUME;
    }
    // 在 SigilForgeVaultCommonBlock 中添加
    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID, int eventParam) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SigilForgeVaultCommonBlockEntity vaultEntity) {
            vaultEntity.onLevelEvent(eventID, eventParam);
        }
        return true;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return createTickerHelper(type, getBlockEntityType(),
                    (pLevel, pPos, pState, pEntity) -> {
                        if (pEntity instanceof SigilForgeVaultCommonBlockEntity entity) {
                            entity.clientTick(pLevel, pPos, pState);
                        }
                    });
        } else {
            return createTickerHelper(type, getBlockEntityType(),
                    (pLevel, pPos, pState, pEntity) -> {
                        if (pEntity instanceof SigilForgeVaultCommonBlockEntity entity) {
                            entity.serverTick((ServerLevel) pLevel, pPos, pState);
                        }
                    });
        }
    }

    public static MapCodec<SigilForgeVaultCommonBlock> getCodec() {
        return MapCodec.unit(SigilForgeVaultCommonBlock::new);
    }
}