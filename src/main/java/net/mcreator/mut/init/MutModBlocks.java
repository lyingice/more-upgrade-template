/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.Sheets;

import net.mcreator.mut.block.*;
import net.mcreator.mut.MutMod;

@EventBusSubscriber
public class MutModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(MutMod.MODID);
	public static final DeferredBlock<Block> STEEL_DEBRIS;
	public static final DeferredBlock<Block> BLUE_DIAMOND_DEBRIS;
	public static final DeferredBlock<Block> NETHER_DEEP_IRON_ORE_BLOCK;
	public static final DeferredBlock<Block> GILDING_DEBRIS;
	public static final DeferredBlock<Block> STEEL_BLOCK;
	public static final DeferredBlock<Block> GILDING_BLOCK;
	public static final DeferredBlock<Block> BLUE_DIAMOND_BLOCK;
	public static final DeferredBlock<Block> ADVANCED_STEEL_BLOCK;
	public static final DeferredBlock<Block> OBSIDIAN_BLOCK;
	public static final DeferredBlock<Block> CRYING_OBSIDIAN_BLOCK;
	public static final DeferredBlock<Block> TEMPLATE_TRADER_BLOCK;
	public static final DeferredBlock<Block> NETHERITE_REDSTONE_BLOCK;
	public static final DeferredBlock<Block> NETHERITE_EMERALD_BLOCK;
	public static final DeferredBlock<Block> NETHERITE_AMETHYST_BLOCK;
	public static final DeferredBlock<Block> DEBRIS_STONE;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE;
	public static final DeferredBlock<Block> FLAME_GOLD_ORE;
	public static final DeferredBlock<Block> METAL_POISONOUS_ORE;
	public static final DeferredBlock<Block> LOG_DEBRIS;
	public static final DeferredBlock<Block> DEBRIS_PLANKS;
	public static final DeferredBlock<Block> SIGIL_FORGE_DIMENSION_PORTAL;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_STAIRS;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_SLAB;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_WALL;
	public static final DeferredBlock<Block> DEBRIS_STONE_STAIRS;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_STAIRS;
	public static final DeferredBlock<Block> DEBRIS_STONE_SLAB;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_SLAB;
	public static final DeferredBlock<Block> DEBRIS_STONE_WALL;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_WALL;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_STAIRS;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_SLAB;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_FENCE;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_FENCE_GATE;
	public static final DeferredBlock<Block> DEBRIS_STONE_PRESSURE_PLATE;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_PRESSURE_PLATE;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_PRESSURE_PLATE;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_PRESSURE_PLATE;
	public static final DeferredBlock<Block> DEBRIS_STONE_BUTTON;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_BUTTON;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_BUTTON;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_BUTTON;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_TRAP_DOOR;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_TRAP_DOOR;
	public static final DeferredBlock<Block> DEBRIS_STONE_TRAP_DOOR;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_TRAP_DOOR;
	public static final DeferredBlock<Block> DEBRIS_STONE_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_WALL_SIGN;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_SIGN;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_WALL_SIGN;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_SIGN;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_WALL_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_WALL_SIGN;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_WALL_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_WALL_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_STONE_WALL_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_WALL_HANGING_SIGN;
	public static final DeferredBlock<Block> DEBRIS_PLANKS_DOOR;
	public static final DeferredBlock<Block> DEBRIS_STONE_DOOR;
	public static final DeferredBlock<Block> DEBRIS_COBBLESTONE_DOOR;
	public static final DeferredBlock<Block> DEBRIS_STONE_BRICKS_DOOR;
	public static final DeferredBlock<Block> DEEP_IRON_RAW_BLOCK;
	public static final DeferredBlock<Block> POSITION_STEEL_BLOCK;
	public static final DeferredBlock<Block> ECHOITE_BLOCK;
	public static final DeferredBlock<Block> SUPER_SMITHING_TABLE;
	public static final DeferredBlock<Block> SIGIL_FORGE_VAULT_COMMON;
	public static final DeferredBlock<Block> SIGIL_FORGE_VAULT_UNIQUE;
	public static final DeferredBlock<Block> SIGIL_FORGE_VAULT_BOSS;
	public static final DeferredBlock<Block> FLAME_GOLD_BLOCK;
	public static final DeferredBlock<Block> NETHERITE_LAPIS_LAZULI_BLOCK;
	public static final DeferredBlock<Block> SIGIL_FORGE_BOSS_TRIAL_SPAWNER;
	public static final DeferredBlock<Block> HETEROCHROMATIC_AMETHYST_BLOCK;
	public static final DeferredBlock<Block> HETEROCHROMATICAMETHYST_1;
	public static final DeferredBlock<Block> HETEROCHROMATICAMETHYST_2;
	public static final DeferredBlock<Block> HETEROCHROMATICAMETHYST_3;
	public static final DeferredBlock<Block> HETEROCHROMATICAMETHYST_4;
	public static final DeferredBlock<Block> HETEROCHROMATICBUDDINGAMETHYST;
	public static final DeferredBlock<Block> DEBRIS_SAPLING;
	public static final DeferredBlock<Block> DEBRIS_LEAVES;
	static {
		STEEL_DEBRIS = REGISTRY.register("steel_debris", SteelDebrisBlock::new);
		BLUE_DIAMOND_DEBRIS = REGISTRY.register("blue_diamond_debris", BlueDiamondDebrisBlock::new);
		NETHER_DEEP_IRON_ORE_BLOCK = REGISTRY.register("nether_deep_iron_ore_block", NetherDeepIronOreBlockBlock::new);
		GILDING_DEBRIS = REGISTRY.register("gilding_debris", GildingDebrisBlock::new);
		STEEL_BLOCK = REGISTRY.register("steel_block", SteelBlockBlock::new);
		GILDING_BLOCK = REGISTRY.register("gilding_block", GildingBlockBlock::new);
		BLUE_DIAMOND_BLOCK = REGISTRY.register("blue_diamond_block", BlueDiamondBlockBlock::new);
		ADVANCED_STEEL_BLOCK = REGISTRY.register("advanced_steel_block", AdvancedSteelBlockBlock::new);
		OBSIDIAN_BLOCK = REGISTRY.register("obsidian_block", ObsidianBlockBlock::new);
		CRYING_OBSIDIAN_BLOCK = REGISTRY.register("crying_obsidian_block", CryingObsidianBlockBlock::new);
		TEMPLATE_TRADER_BLOCK = REGISTRY.register("template_trader_block", TemplateTraderBlockBlock::new);
		NETHERITE_REDSTONE_BLOCK = REGISTRY.register("netherite_redstone_block", NetheriteRedstoneBlockBlock::new);
		NETHERITE_EMERALD_BLOCK = REGISTRY.register("netherite_emerald_block", NetheriteEmeraldBlockBlock::new);
		NETHERITE_AMETHYST_BLOCK = REGISTRY.register("netherite_amethyst_block", NetheriteAmethystBlockBlock::new);
		DEBRIS_STONE = REGISTRY.register("debris_stone", DebrisStoneBlock::new);
		DEBRIS_COBBLESTONE = REGISTRY.register("debris_cobblestone", DebrisCobblestoneBlock::new);
		FLAME_GOLD_ORE = REGISTRY.register("flame_gold_ore", FlameGoldOreBlock::new);
		METAL_POISONOUS_ORE = REGISTRY.register("metal_poisonous_ore", MetalPoisonousOreBlock::new);
		LOG_DEBRIS = REGISTRY.register("log_debris", LogDebrisBlock::new);
		DEBRIS_PLANKS = REGISTRY.register("debris_planks", DebrisPlanksBlock::new);
		SIGIL_FORGE_DIMENSION_PORTAL = REGISTRY.register("sigil_forge_dimension_portal", SigilForgeDimensionPortalBlock::new);
		DEBRIS_STONE_BRICKS = REGISTRY.register("debris_stone_bricks", DebrisStoneBricksBlock::new);
		DEBRIS_STONE_BRICKS_STAIRS = REGISTRY.register("debris_stone_bricks_stairs", DebrisStoneBricksStairsBlock::new);
		DEBRIS_STONE_BRICKS_SLAB = REGISTRY.register("debris_stone_bricks_slab", DebrisStoneBricksSlabBlock::new);
		DEBRIS_STONE_BRICKS_WALL = REGISTRY.register("debris_stone_bricks_wall", DebrisStoneBricksWallBlock::new);
		DEBRIS_STONE_STAIRS = REGISTRY.register("debris_stone_stairs", DebrisStoneStairsBlock::new);
		DEBRIS_COBBLESTONE_STAIRS = REGISTRY.register("debris_cobblestone_stairs", DebrisCobblestoneStairsBlock::new);
		DEBRIS_STONE_SLAB = REGISTRY.register("debris_stone_slab", DebrisStoneSlabBlock::new);
		DEBRIS_COBBLESTONE_SLAB = REGISTRY.register("debris_cobblestone_slab", DebrisCobblestoneSlabBlock::new);
		DEBRIS_STONE_WALL = REGISTRY.register("debris_stone_wall", DebrisStoneWallBlock::new);
		DEBRIS_COBBLESTONE_WALL = REGISTRY.register("debris_cobblestone_wall", DebrisCobblestoneWallBlock::new);
		DEBRIS_PLANKS_STAIRS = REGISTRY.register("debris_planks_stairs", DebrisPlanksStairsBlock::new);
		DEBRIS_PLANKS_SLAB = REGISTRY.register("debris_planks_slab", DebrisPlanksSlabBlock::new);
		DEBRIS_PLANKS_FENCE = REGISTRY.register("debris_planks_fence", DebrisPlanksFenceBlock::new);
		DEBRIS_PLANKS_FENCE_GATE = REGISTRY.register("debris_planks_fence_gate", DebrisPlanksFenceGateBlock::new);
		DEBRIS_STONE_PRESSURE_PLATE = REGISTRY.register("debris_stone_pressure_plate", DebrisStonePressurePlateBlock::new);
		DEBRIS_COBBLESTONE_PRESSURE_PLATE = REGISTRY.register("debris_cobblestone_pressure_plate", DebrisCobblestonePressurePlateBlock::new);
		DEBRIS_PLANKS_PRESSURE_PLATE = REGISTRY.register("debris_planks_pressure_plate", DebrisPlanksPressurePlateBlock::new);
		DEBRIS_STONE_BRICKS_PRESSURE_PLATE = REGISTRY.register("debris_stone_bricks_pressure_plate", DebrisStoneBricksPressurePlateBlock::new);
		DEBRIS_STONE_BUTTON = REGISTRY.register("debris_stone_button", DebrisStoneButtonBlock::new);
		DEBRIS_COBBLESTONE_BUTTON = REGISTRY.register("debris_cobblestone_button", DebrisCobblestoneButtonBlock::new);
		DEBRIS_PLANKS_BUTTON = REGISTRY.register("debris_planks_button", DebrisPlanksButtonBlock::new);
		DEBRIS_STONE_BRICKS_BUTTON = REGISTRY.register("debris_stone_bricks_button", DebrisStoneBricksButtonBlock::new);
		DEBRIS_STONE_BRICKS_TRAP_DOOR = REGISTRY.register("debris_stone_bricks_trap_door", DebrisStoneBricksTrapDoorBlock::new);
		DEBRIS_PLANKS_TRAP_DOOR = REGISTRY.register("debris_planks_trap_door", DebrisPlanksTrapDoorBlock::new);
		DEBRIS_STONE_TRAP_DOOR = REGISTRY.register("debris_stone_trap_door", DebrisStoneTrapDoorBlock::new);
		DEBRIS_COBBLESTONE_TRAP_DOOR = REGISTRY.register("debris_cobblestone_trap_door", DebrisCobblestoneTrapDoorBlock::new);
		DEBRIS_STONE_SIGN = REGISTRY.register("debris_stone_sign", DebrisStoneSignBlock::new);
		DEBRIS_STONE_WALL_SIGN = REGISTRY.register("debris_stone_wall_sign", DebrisStoneWallSignBlock::new);
		DEBRIS_COBBLESTONE_SIGN = REGISTRY.register("debris_cobblestone_sign", DebrisCobblestoneSignBlock::new);
		DEBRIS_COBBLESTONE_WALL_SIGN = REGISTRY.register("debris_cobblestone_wall_sign", DebrisCobblestoneWallSignBlock::new);
		DEBRIS_PLANKS_SIGN = REGISTRY.register("debris_planks_sign", DebrisPlanksSignBlock::new);
		DEBRIS_PLANKS_WALL_SIGN = REGISTRY.register("debris_planks_wall_sign", DebrisPlanksWallSignBlock::new);
		DEBRIS_STONE_BRICKS_SIGN = REGISTRY.register("debris_stone_bricks_sign", DebrisStoneBricksSignBlock::new);
		DEBRIS_STONE_BRICKS_WALL_SIGN = REGISTRY.register("debris_stone_bricks_wall_sign", DebrisStoneBricksWallSignBlock::new);
		DEBRIS_PLANKS_HANGING_SIGN = REGISTRY.register("debris_planks_hanging_sign", DebrisPlanksHangingSignBlock::new);
		DEBRIS_PLANKS_WALL_HANGING_SIGN = REGISTRY.register("debris_planks_wall_hanging_sign", DebrisPlanksWallHangingSignBlock::new);
		DEBRIS_STONE_BRICKS_HANGING_SIGN = REGISTRY.register("debris_stone_bricks_hanging_sign", DebrisStoneBricksHangingSignBlock::new);
		DEBRIS_STONE_BRICKS_WALL_HANGING_SIGN = REGISTRY.register("debris_stone_bricks_wall_hanging_sign", DebrisStoneBricksWallHangingSignBlock::new);
		DEBRIS_STONE_HANGING_SIGN = REGISTRY.register("debris_stone_hanging_sign", DebrisStoneHangingSignBlock::new);
		DEBRIS_STONE_WALL_HANGING_SIGN = REGISTRY.register("debris_stone_wall_hanging_sign", DebrisStoneWallHangingSignBlock::new);
		DEBRIS_COBBLESTONE_HANGING_SIGN = REGISTRY.register("debris_cobblestone_hanging_sign", DebrisCobblestoneHangingSignBlock::new);
		DEBRIS_COBBLESTONE_WALL_HANGING_SIGN = REGISTRY.register("debris_cobblestone_wall_hanging_sign", DebrisCobblestoneWallHangingSignBlock::new);
		DEBRIS_PLANKS_DOOR = REGISTRY.register("debris_planks_door", DebrisPlanksDoorBlock::new);
		DEBRIS_STONE_DOOR = REGISTRY.register("debris_stone_door", DebrisStoneDoorBlock::new);
		DEBRIS_COBBLESTONE_DOOR = REGISTRY.register("debris_cobblestone_door", DebrisCobblestoneDoorBlock::new);
		DEBRIS_STONE_BRICKS_DOOR = REGISTRY.register("debris_stone_bricks_door", DebrisStoneBricksDoorBlock::new);
		DEEP_IRON_RAW_BLOCK = REGISTRY.register("deep_iron_raw_block", DeepIronRawBlockBlock::new);
		POSITION_STEEL_BLOCK = REGISTRY.register("position_steel_block", PositionSteelBlockBlock::new);
		ECHOITE_BLOCK = REGISTRY.register("echoite_block", EchoiteBlockBlock::new);
		SUPER_SMITHING_TABLE = REGISTRY.register("super_smithing_table", SuperSmithingTableBlock::new);
		SIGIL_FORGE_VAULT_COMMON = REGISTRY.register("sigil_forge_vault_common", SigilForgeVaultCommonBlock::new);
		SIGIL_FORGE_VAULT_UNIQUE = REGISTRY.register("sigil_forge_vault_unique", SigilForgeVaultUniqueBlock::new);
		SIGIL_FORGE_VAULT_BOSS = REGISTRY.register("sigil_forge_vault_boss", SigilForgeVaultBossBlock::new);
		FLAME_GOLD_BLOCK = REGISTRY.register("flame_gold_block", FlameGoldBlockBlock::new);
		NETHERITE_LAPIS_LAZULI_BLOCK = REGISTRY.register("netherite_lapis_lazuli_block", NetheriteLapisLazuliBlockBlock::new);
		SIGIL_FORGE_BOSS_TRIAL_SPAWNER = REGISTRY.register("sigil_forge_boss_trial_spawner", SigilForgeBossTrialSpawnerBlock::new);
		HETEROCHROMATIC_AMETHYST_BLOCK = REGISTRY.register("heterochromatic_amethyst_block", HeterochromaticAmethystBlockBlock::new);
		HETEROCHROMATICAMETHYST_1 = REGISTRY.register("heterochromaticamethyst_1", Heterochromaticamethyst1Block::new);
		HETEROCHROMATICAMETHYST_2 = REGISTRY.register("heterochromaticamethyst_2", Heterochromaticamethyst2Block::new);
		HETEROCHROMATICAMETHYST_3 = REGISTRY.register("heterochromaticamethyst_3", Heterochromaticamethyst3Block::new);
		HETEROCHROMATICAMETHYST_4 = REGISTRY.register("heterochromaticamethyst_4", Heterochromaticamethyst4Block::new);
		HETEROCHROMATICBUDDINGAMETHYST = REGISTRY.register("heterochromaticbuddingamethyst", HeterochromaticbuddingamethystBlock::new);
		DEBRIS_SAPLING = REGISTRY.register("debris_sapling", DebrisSaplingBlock::new);
		DEBRIS_LEAVES = REGISTRY.register("debris_leaves", DebrisLeavesBlock::new);
	}

	// Start of user code block custom blocks
	@EventBusSubscriber
	public static class AmethystSetup {
		@SubscribeEvent
		public static void onCommonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
			HeterochromaticbuddingamethystBlock.CLUSTERS[0] = HETEROCHROMATICAMETHYST_1.get();
			HeterochromaticbuddingamethystBlock.CLUSTERS[1] = HETEROCHROMATICAMETHYST_2.get();
			HeterochromaticbuddingamethystBlock.CLUSTERS[2] = HETEROCHROMATICAMETHYST_3.get();
			HeterochromaticbuddingamethystBlock.CLUSTERS[3] = HETEROCHROMATICAMETHYST_4.get();
		}
	}

	public static final DeferredBlock<Block> SIGIL_FORGE_TRIAL_SPAWNER = REGISTRY.register("sigil_forge_trial_spawner", SigilForgeTrialSpawnerBlock::new);
	public static final DeferredBlock<Block> SIGIL_FORGE_UNIQUE_TRIAL_SPAWNER = REGISTRY.register("sigil_forge_unique_trial_spawner", SigilForgeUniqueTrialSpawnerBlock::new);

	// End of user code block custom blocks
	@EventBusSubscriber(Dist.CLIENT)
	public static class BlocksClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_STONE_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_COBBLESTONE_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_PLANKS_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_STONE_BRICKS_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_PLANKS_HANGING_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_STONE_BRICKS_HANGING_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_STONE_HANGING_SIGN_WOOD_TYPE);
			Sheets.addWoodType(MutModWoodTypes.DEBRIS_COBBLESTONE_HANGING_SIGN_WOOD_TYPE);
		}
	}

	@SubscribeEvent
	public static void registerSigns(BlockEntityTypeAddBlocksEvent event) {
		event.modify(BlockEntityType.SIGN, DEBRIS_STONE_SIGN.get(), DEBRIS_STONE_WALL_SIGN.get());
		event.modify(BlockEntityType.SIGN, DEBRIS_COBBLESTONE_SIGN.get(), DEBRIS_COBBLESTONE_WALL_SIGN.get());
		event.modify(BlockEntityType.SIGN, DEBRIS_PLANKS_SIGN.get(), DEBRIS_PLANKS_WALL_SIGN.get());
		event.modify(BlockEntityType.SIGN, DEBRIS_STONE_BRICKS_SIGN.get(), DEBRIS_STONE_BRICKS_WALL_SIGN.get());
		event.modify(BlockEntityType.HANGING_SIGN, DEBRIS_PLANKS_HANGING_SIGN.get(), DEBRIS_PLANKS_WALL_HANGING_SIGN.get());
		event.modify(BlockEntityType.HANGING_SIGN, DEBRIS_STONE_BRICKS_HANGING_SIGN.get(), DEBRIS_STONE_BRICKS_WALL_HANGING_SIGN.get());
		event.modify(BlockEntityType.HANGING_SIGN, DEBRIS_STONE_HANGING_SIGN.get(), DEBRIS_STONE_WALL_HANGING_SIGN.get());
		event.modify(BlockEntityType.HANGING_SIGN, DEBRIS_COBBLESTONE_HANGING_SIGN.get(), DEBRIS_COBBLESTONE_WALL_HANGING_SIGN.get());
	}
}