package net.mcreator.mut.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.registries.Registries;

import net.mcreator.mut.block.entity.SigilForgeVaultUniqueBlockEntity;
import net.mcreator.mut.init.MutModBlockEntities;

public class SigilForgeVaultUniqueBlock extends SigilForgeVaultCommonBlock {

    public static final ResourceKey<LootTable> UNIQUE_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("mut", "vault/unique"));

    public SigilForgeVaultUniqueBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.VAULT)
                .strength(50f)
                .lightLevel(s -> s.getValue(VaultBlock.STATE).lightLevel())
                .instrument(NoteBlockInstrument.BASEDRUM).noOcclusion()                              // ← 已有
                .isViewBlocking((bs, br, bp) -> false)      // ← 加这个
                .isRedstoneConductor((bs, br, bp) -> false));

    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SigilForgeVaultUniqueBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<?> getBlockEntityType() {
        return MutModBlockEntities.SIGIL_FORGE_VAULT_UNIQUE.get();
    }
}