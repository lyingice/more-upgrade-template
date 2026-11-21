package net.mcreator.mut.porting.mercenary;

import net.mcreator.mut.MutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 猪灵雇佣兵实体注册。 */
public final class MercenaryEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MutMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PiglinMercenaryEntity>> PIGLIN_MERCENARY =
            ENTITIES.register("piglin_mercenary",
                    () -> EntityType.Builder.of(PiglinMercenaryEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("piglin_mercenary"));

    private MercenaryEntities() {
    }
}
