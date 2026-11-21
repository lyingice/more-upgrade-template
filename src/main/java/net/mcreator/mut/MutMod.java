package net.mcreator.mut;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.TickTask;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.mut.trait.TraitEventHandler;
import net.mcreator.mut.init.*;
import net.mcreator.mut.event.SpearAffixListener;
import net.mcreator.mut.event.EnergyConversionHandler;
import net.mcreator.mut.config.MutCrossbowLoadCountConfig;
import net.mcreator.mut.config.AffixConfig;
import net.mcreator.mut.compat.spearcore.SpearcoreIntegration;
import net.mcreator.mut.command.AddAffixCommand;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;

@Mod("mut")
public class MutMod {
	public static final Logger LOGGER = LogManager.getLogger(MutMod.class);
	public static final String MODID = "mut";

	public MutMod(IEventBus modEventBus) {
		// Start of user code block mod constructor
		SpearcoreIntegration.bootstrap(modEventBus);
		// End of user code block mod constructor
		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::registerNetworking);
		MutModSounds.REGISTRY.register(modEventBus);
		MutModBlocks.REGISTRY.register(modEventBus);
		MutModBlockEntities.REGISTRY.register(modEventBus);
		MutModItems.REGISTRY.register(modEventBus);
		MutModEntities.REGISTRY.register(modEventBus);
		MutModTabs.REGISTRY.register(modEventBus);
		MutModMobEffects.REGISTRY.register(modEventBus);
		MutModMenus.REGISTRY.register(modEventBus);
		MutModVillagerProfessions.PROFESSIONS.register(modEventBus);
		MutModAttributes.REGISTRY.register(modEventBus);
		// Start of user code block mod init
		// 移植的锻造模板（独立于 MCreator 生成的 MutModItems，重新生成代码时不会丢失）
		net.mcreator.mut.porting.smithing.MutSmithingTemplateItems.REGISTRY.register(modEventBus);
		// 移植的马铠 / 狼铠展示架
		net.mcreator.mut.porting.armorstand.MutArmorStandItems.ITEMS.register(modEventBus);
		net.mcreator.mut.porting.armorstand.MutArmorStandEntities.ENTITIES.register(modEventBus);
		ModContainer container = ModLoadingContext.get().getActiveContainer();
		container.registerConfig(ModConfig.Type.COMMON, AffixConfig.SPEC, "mut_affix_coefficients.toml");
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, MutCrossbowLoadCountConfig.CONFIG_SPEC, "mut_crossbow_load_count.toml");
		MutModItems.ENTITY_REGISTRY.register(modEventBus);
		NeoForge.EVENT_BUS.register(new EnergyConversionHandler());
		MutModParticles.PARTICLES.register(modEventBus);
		NeoForge.EVENT_BUS.register(new TraitEventHandler());
		// 套装效果（护甲四件套）
		NeoForge.EVENT_BUS.register(new net.mcreator.mut.armorset.ArmorSetEventHandler());
		// 遗迹傀儡实体（独立注册，避免被 MCreator 重新生成覆盖）
		net.mcreator.mut.porting.ruingolem.RuinGolemEntities.ENTITIES.register(modEventBus);
		net.mcreator.mut.porting.ruingolem.RuinGolemItems.ITEMS.register(modEventBus);
		net.mcreator.mut.porting.ruingolem.RuinGolemBlocks.BLOCKS.register(modEventBus);
		net.mcreator.mut.porting.ruingolem.RuinGolemBlocks.BLOCK_ENTITIES.register(modEventBus);
		// 猪灵雇佣兵
		net.mcreator.mut.porting.mercenary.MercenaryEntities.ENTITIES.register(modEventBus);
		net.mcreator.mut.porting.mercenary.MercenaryItems.ITEMS.register(modEventBus);
		// 奇迹装备 / 金质号角 / 奇迹锻造模板（空壳，能力后续再接）
		net.mcreator.mut.miracle.MiracleItems.ITEMS.register(modEventBus);
		// 0.5→1.0 新生物（蛮兵要塞 / 深暗蛛穴 / 模板圣庙 / 恶震渊井）
		net.mcreator.mut.beastiary.BeastiaryEntities.ENTITIES.register(modEventBus);
		net.mcreator.mut.beastiary.BeastiaryItems.ITEMS.register(modEventBus);
		net.mcreator.mut.beastiary.BeastiaryEffects.EFFECTS.register(modEventBus);
		// 农夫乐事 / 天境联动（类加载隔离：模组不在时完全跳过）
		net.mcreator.mut.compat.CompatBootstrap.init(modEventBus);
		if (ModList.get().isLoaded("spearcore")) {
			NeoForge.EVENT_BUS.register(SpearAffixListener.class);
		}
		// End of user code block mod init
	}

	// Start of user code block mod methods
	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		AddAffixCommand.register(event.getDispatcher());
	}

	// End of user code block mod methods
	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	private static final Queue<IntObjectPair<Runnable>> workToBeScheduled = new ConcurrentLinkedQueue<>();
	private static final PriorityQueue<TickTask> workQueue = new PriorityQueue<>(Comparator.comparingInt(TickTask::getTick));

	public static void queueServerWork(int delay, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workToBeScheduled.add(new IntObjectImmutablePair<>(delay, action));
	}

	@SubscribeEvent
	public void tick(ServerTickEvent.Post event) {
		int currentTick = event.getServer().getTickCount();
		IntObjectPair<Runnable> work;
		while ((work = workToBeScheduled.poll()) != null) {
			workQueue.add(new TickTask(currentTick + work.leftInt(), work.right()));
		}
		while (!workQueue.isEmpty() && currentTick >= workQueue.peek().getTick()) {
			workQueue.poll().run();
		}
	}

	public static class CuriosApiHelper {
		private static final EntityCapability<IItemHandler, Void> CURIOS_INVENTORY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("curios", "item_handler"), IItemHandler.class);

		public static IItemHandler getCuriosInventory(Player player) {
			if (ModList.get().isLoaded("curios")) {
				return player.getCapability(CURIOS_INVENTORY);
			}
			return null;
		}

		public static boolean isCurioItem(ItemStack itemstack) {
			return BuiltInRegistries.ITEM.getTagNames().filter(tagKey -> tagKey.location().getNamespace().equals("curios")).anyMatch(itemstack::is);
		}
	}
}