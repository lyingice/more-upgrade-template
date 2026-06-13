package net.mcreator.mut.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.mut.block.SigilForgeVaultUniqueBlock;
import net.mcreator.mut.init.MutModBlockEntities;

import java.util.Optional;

public class SigilForgeVaultUniqueBlockEntity extends SigilForgeVaultCommonBlockEntity {

    public SigilForgeVaultUniqueBlockEntity(BlockPos pos, BlockState state) {
        super(MutModBlockEntities.SIGIL_FORGE_VAULT_UNIQUE.get(), pos, state);
    }

    @Override
    protected VaultConfig createConfig() {
        return new VaultConfig(
                SigilForgeVaultUniqueBlock.UNIQUE_LOOT_TABLE,
                4.0, 4.5,
                new ItemStack(Items.OMINOUS_TRIAL_KEY),
                Optional.empty(),
                net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
                net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
        );
    }
}