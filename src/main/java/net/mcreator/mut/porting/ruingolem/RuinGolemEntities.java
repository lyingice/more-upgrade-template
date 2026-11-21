package net.mcreator.mut.porting.ruingolem;

import net.mcreator.mut.MutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 遗迹傀儡实体注册（独立于 MCreator 生成的 MutModEntities）。 */
public final class RuinGolemEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MutMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ObsidianRuinGolemEntity>> OBSIDIAN_RUIN_GOLEM =
            ENTITIES.register("obsidian_ruin_golem",
                    () -> EntityType.Builder.of(ObsidianRuinGolemEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 2.7F)
                            .clientTrackingRange(10)
                            .build("obsidian_ruin_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<CryingObsidianRuinGolemEntity>> CRYING_OBSIDIAN_RUIN_GOLEM =
            ENTITIES.register("crying_obsidian_ruin_golem",
                    () -> EntityType.Builder.of(CryingObsidianRuinGolemEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 2.7F)
                            .clientTrackingRange(10)
                            .build("crying_obsidian_ruin_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<RockRuinGolemEntity>> ROCK_RUIN_GOLEM =
            ENTITIES.register("rock_ruin_golem",
                    () -> EntityType.Builder.of(RockRuinGolemEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 2.7F)
                            .clientTrackingRange(10)
                            .build("rock_ruin_golem"));

    private RuinGolemEntities() {
    }
}
