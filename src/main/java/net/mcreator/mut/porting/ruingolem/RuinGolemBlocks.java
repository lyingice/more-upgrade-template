package net.mcreator.mut.porting.ruingolem;

import net.mcreator.mut.MutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 岩石傀儡"石像"方块的注册。 */
public final class RuinGolemBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MutMod.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MutMod.MODID);

    public static final DeferredBlock<RockGolemBlock> ROCK_GOLEM_BLOCK =
            BLOCKS.register("rock_golem_block", () -> new RockGolemBlock());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RockGolemBlockEntity>> ROCK_GOLEM_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rock_golem_block",
                    () -> BlockEntityType.Builder.of(RockGolemBlockEntity::new, ROCK_GOLEM_BLOCK.get()).build(null));

    private RuinGolemBlocks() {
    }
}
