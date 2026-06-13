package net.mcreator.mut.block;

import net.mcreator.mut.block.entity.SigilForgeUniqueTrialSpawnerBlockEntity;
import net.mcreator.mut.init.MutModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SigilForgeUniqueTrialSpawnerBlock extends SigilForgeTrialSpawnerBlock {
    public SigilForgeUniqueTrialSpawnerBlock() {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SigilForgeUniqueTrialSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, MutModBlockEntities.SIGIL_FORGE_UNIQUE_TRIAL_SPAWNER.get(),
                (pLevel, pPos, pState, pEntity) -> {
                    if (pEntity instanceof SigilForgeUniqueTrialSpawnerBlockEntity spawner) {
                        spawner.serverTick((ServerLevel) pLevel, pPos, pState);
                    }
                });
    }
}