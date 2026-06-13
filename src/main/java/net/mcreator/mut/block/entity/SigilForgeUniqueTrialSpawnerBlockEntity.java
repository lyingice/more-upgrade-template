package net.mcreator.mut.block.entity;

import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerConfig;
import net.mcreator.mut.block.entity.trial_spawner.TrialSpawnerLogic;
import net.mcreator.mut.block.entity.trial_spawner.UniqueTrialSpawnerConfig;
import net.mcreator.mut.init.MutModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SigilForgeUniqueTrialSpawnerBlockEntity extends SigilForgeTrialSpawnerBlockEntity {
    public SigilForgeUniqueTrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(MutModBlockEntities.SIGIL_FORGE_UNIQUE_TRIAL_SPAWNER.get(), pos, state, new UniqueTrialSpawnerConfig());
    }
}

