/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.mut.block.entity.*;
import net.mcreator.mut.MutMod;

@EventBusSubscriber
public class MutModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MutMod.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TemplateTraderBlockBlockEntity>> TEMPLATE_TRADER_BLOCK = register("template_trader_block", MutModBlocks.TEMPLATE_TRADER_BLOCK, TemplateTraderBlockBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperSmithingTableBlockEntity>> SUPER_SMITHING_TABLE = register("super_smithing_table", MutModBlocks.SUPER_SMITHING_TABLE, SuperSmithingTableBlockEntity::new);
	// Start of user code block custom block entities
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SigilForgeVaultCommonBlockEntity>> SIGIL_FORGE_VAULT_COMMON = register("sigil_forge_vault_common", MutModBlocks.SIGIL_FORGE_VAULT_COMMON,
			SigilForgeVaultCommonBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SigilForgeVaultUniqueBlockEntity>> SIGIL_FORGE_VAULT_UNIQUE = register("sigil_forge_vault_unique", MutModBlocks.SIGIL_FORGE_VAULT_UNIQUE,
			SigilForgeVaultUniqueBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SigilForgeVaultBossBlockEntity>> SIGIL_FORGE_VAULT_BOSS = register("sigil_forge_vault_boss", MutModBlocks.SIGIL_FORGE_VAULT_BOSS, SigilForgeVaultBossBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SigilForgeBossTrialSpawnerBlockEntity>> SIGIL_FORGE_BOSS_TRIAL_SPAWNER = register("sigil_forge_boss_trial_spawner", MutModBlocks.SIGIL_FORGE_BOSS_TRIAL_SPAWNER,
			SigilForgeBossTrialSpawnerBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SigilForgeTrialSpawnerBlockEntity>> SIGIL_FORGE_TRIAL_SPAWNER = register("sigil_forge_trial_spawner", MutModBlocks.SIGIL_FORGE_TRIAL_SPAWNER,
			SigilForgeTrialSpawnerBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SigilForgeUniqueTrialSpawnerBlockEntity>> SIGIL_FORGE_UNIQUE_TRIAL_SPAWNER = register("sigil_forge_unique_trial_spawner", MutModBlocks.SIGIL_FORGE_UNIQUE_TRIAL_SPAWNER,
			SigilForgeUniqueTrialSpawnerBlockEntity::new);

	// End of user code block custom block entities
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TEMPLATE_TRADER_BLOCK.get(), SidedInvWrapper::new);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SUPER_SMITHING_TABLE.get(), SidedInvWrapper::new);
	}
}