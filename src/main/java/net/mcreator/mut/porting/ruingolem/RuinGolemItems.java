package net.mcreator.mut.porting.ruingolem;

import net.mcreator.mut.MutMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 遗迹傀儡刷怪蛋（独立于 MCreator 生成的 MutModItems）。 */
public final class RuinGolemItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    /** 背景色 / 斑点色，格式为 0xRRGGBB */
    public static final DeferredItem<Item> OBSIDIAN_RUIN_GOLEM_SPAWN_EGG =
            ITEMS.register("obsidian_ruin_golem_spawn_egg",
                    () -> new DeferredSpawnEggItem(RuinGolemEntities.OBSIDIAN_RUIN_GOLEM,
                            0x14001F, 0x3F2A5C, new Item.Properties()));

    public static final DeferredItem<Item> CRYING_OBSIDIAN_RUIN_GOLEM_SPAWN_EGG =
            ITEMS.register("crying_obsidian_ruin_golem_spawn_egg",
                    () -> new DeferredSpawnEggItem(RuinGolemEntities.CRYING_OBSIDIAN_RUIN_GOLEM,
                            0x2A0A2E, 0x8B3DD1, new Item.Properties()));

    public static final DeferredItem<Item> ROCK_RUIN_GOLEM_SPAWN_EGG =
            ITEMS.register("rock_ruin_golem_spawn_egg",
                    () -> new DeferredSpawnEggItem(RuinGolemEntities.ROCK_RUIN_GOLEM,
                            0x7A7A7A, 0x4C4C4C, new Item.Properties()));

    /** 石像方块对应的物品 */
    public static final DeferredItem<BlockItem> ROCK_GOLEM_BLOCK_ITEM =
            ITEMS.register("rock_golem_block",
                    () -> new BlockItem(RuinGolemBlocks.ROCK_GOLEM_BLOCK.get(), new Item.Properties()));

    private RuinGolemItems() {
    }
}
