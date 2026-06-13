package net.mcreator.mut.block.entity;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.mut.block.SigilForgeVaultBossBlock;
import net.mcreator.mut.init.MutModBlockEntities;

import java.util.Optional;

public class SigilForgeVaultBossBlockEntity extends SigilForgeVaultCommonBlockEntity {

    public SigilForgeVaultBossBlockEntity(BlockPos pos, BlockState state) {
        super(MutModBlockEntities.SIGIL_FORGE_VAULT_BOSS.get(), pos, state);
    }

    @Override
    protected VaultConfig createConfig() {
        return new VaultConfig(
                SigilForgeVaultBossBlock.BOSS_LOOT_TABLE,
                6.0, 6.5,
                new ItemStack(MutModItems.BOSS_TRIAL_KEY.get()),
                Optional.empty(),
                net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
                net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
        );
    }
}