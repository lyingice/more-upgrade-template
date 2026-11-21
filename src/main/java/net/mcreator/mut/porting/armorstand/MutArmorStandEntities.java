package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.MutMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 展示架实体注册（独立于 MCreator 生成的 MutModEntities）。 */
public final class MutArmorStandEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MutMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<HorseArmorStandEntity>> HORSE_ARMOR_STAND =
            ENTITIES.register(
                    "horse_armor_stand",
                    () -> EntityType.Builder.of(HorseArmorStandEntity::new, MobCategory.MISC)
                            .sized(0.75F, 1.5F)
                            .clientTrackingRange(10)
                            .build("horse_armor_stand")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<WolfArmorStandEntity>> WOLF_ARMOR_STAND =
            ENTITIES.register(
                    "wolf_armor_stand",
                    () -> EntityType.Builder.of(WolfArmorStandEntity::new, MobCategory.MISC)
                            .sized(0.6F, 0.9F)
                            .clientTrackingRange(10)
                            .build("wolf_armor_stand")
            );

    private MutArmorStandEntities() {
    }
}
