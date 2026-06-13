/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

import net.mcreator.mut.entity.*;
import net.mcreator.mut.MutMod;

@EventBusSubscriber
public class MutModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, MutMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<AborigineZombieEntity>> ABORIGINE_ZOMBIE = register("aborigine_zombie",
			EntityType.Builder.<AborigineZombieEntity>of(AborigineZombieEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).ridingOffset(-0.6f).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<RedLightningCreeperEntity>> RED_LIGHTNING_CREEPER = register("red_lightning_creeper",
			EntityType.Builder.<RedLightningCreeperEntity>of(RedLightningCreeperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.7f));
	public static final DeferredHolder<EntityType<?>, EntityType<TravelerPhantomEntity>> TRAVELER_PHANTOM = register("traveler_phantom",
			EntityType.Builder.<TravelerPhantomEntity>of(TravelerPhantomEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).sized(0.9f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<LittleCreeperEntity>> LITTLE_CREEPER = register("little_creeper",
			EntityType.Builder.<LittleCreeperEntity>of(LittleCreeperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1f));
	public static final DeferredHolder<EntityType<?>, EntityType<EliteAborigineZombieEntity>> ELITE_ABORIGINE_ZOMBIE = register("elite_aborigine_zombie",
			EntityType.Builder.<EliteAborigineZombieEntity>of(EliteAborigineZombieEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).ridingOffset(-0.6f).sized(0.6f, 1.8f));
	// Start of user code block custom entities
	public static final DeferredHolder<EntityType<?>, EntityType<SigilForgeZombieBossEntity>> SIGIL_FORGE_ZOMBIE_BOSS = register("sigil_forge_zombie_boss",
			EntityType.Builder.<SigilForgeZombieBossEntity>of(SigilForgeZombieBossEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.95f));

	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		AborigineZombieEntity.init(event);
		RedLightningCreeperEntity.init(event);
		TravelerPhantomEntity.init(event);
		LittleCreeperEntity.init(event);
		EliteAborigineZombieEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ABORIGINE_ZOMBIE.get(), AborigineZombieEntity.createAttributes().build());
		event.put(RED_LIGHTNING_CREEPER.get(), RedLightningCreeperEntity.createAttributes().build());
		event.put(TRAVELER_PHANTOM.get(), TravelerPhantomEntity.createAttributes().build());
		event.put(LITTLE_CREEPER.get(), LittleCreeperEntity.createAttributes().build());
		event.put(ELITE_ABORIGINE_ZOMBIE.get(), EliteAborigineZombieEntity.createAttributes().build());
	}
}