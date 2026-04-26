/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.mcreator.mut.block.*;
import net.mcreator.mut.MutMod;

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
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}