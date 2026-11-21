package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 0.5→1.0 版本新增生物的统一注册表（独立于 MCreator 生成的文件）。 */
public final class BeastiaryEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MutMod.MODID);

    // ==== 蛮兵要塞 ====
    public static final DeferredHolder<EntityType<?>, EntityType<TuskedWarlordEntity>> TUSKED_WARLORD =
            ENTITIES.register("tusked_warlord", () -> EntityType.Builder
                    .of(TuskedWarlordEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.4F).clientTrackingRange(10).build("tusked_warlord"));

    public static final DeferredHolder<EntityType<?>, EntityType<RangedPiglinMercenaryEntity>> PIGLIN_MERCENARY_RANGED =
            ENTITIES.register("piglin_mercenary_ranged", () -> EntityType.Builder
                    .of(RangedPiglinMercenaryEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("piglin_mercenary_ranged"));

    public static final DeferredHolder<EntityType<?>, EntityType<DefenderPiglinMercenaryEntity>> PIGLIN_MERCENARY_DEFENDER =
            ENTITIES.register("piglin_mercenary_defender", () -> EntityType.Builder
                    .of(DefenderPiglinMercenaryEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 1.95F).clientTrackingRange(8).build("piglin_mercenary_defender"));

    public static final DeferredHolder<EntityType<?>, EntityType<TuskJavelinEntity>> TUSK_JAVELIN =
            ENTITIES.register("tusk_javelin", () -> EntityType.Builder
                    .<TuskJavelinEntity>of(TuskJavelinEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(6).build("tusk_javelin"));

    // ==== 深暗蛛穴 ====
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderQueenEntity>> SPIDER_QUEEN =
            ENTITIES.register("spider_queen", () -> EntityType.Builder
                    .of(SpiderQueenEntity::new, MobCategory.MONSTER)
                    .sized(4.8F, 3.0F).clientTrackingRange(10).build("spider_queen"));

    public static final DeferredHolder<EntityType<?>, EntityType<DarkSpiderEntity>> DARK_SPIDER =
            ENTITIES.register("dark_spider", () -> EntityType.Builder
                    .of(DarkSpiderEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F).clientTrackingRange(10).build("dark_spider"));

    public static final DeferredHolder<EntityType<?>, EntityType<DeepLeapingSpiderEntity>> DEEP_LEAPING_SPIDER =
            ENTITIES.register("deep_leaping_spider", () -> EntityType.Builder
                    .of(DeepLeapingSpiderEntity::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F).clientTrackingRange(10).build("deep_leaping_spider"));

    // ==== 模板圣庙 ====
    public static final DeferredHolder<EntityType<?>, EntityType<FallenTemplateSageEntity>> FALLEN_TEMPLATE_SAGE =
            ENTITIES.register("fallen_template_sage", () -> EntityType.Builder
                    .of(FallenTemplateSageEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("fallen_template_sage"));

    public static final DeferredHolder<EntityType<?>, EntityType<TemplateGodRemainsEntity>> TEMPLATE_GOD_REMAINS =
            ENTITIES.register("template_god_remains", () -> EntityType.Builder
                    .of(TemplateGodRemainsEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).clientTrackingRange(12).fireImmune().build("template_god_remains"));

    public static final DeferredHolder<EntityType<?>, EntityType<FallenArtisanEntity>> FALLEN_ARTISAN =
            ENTITIES.register("fallen_artisan", () -> EntityType.Builder
                    .of(FallenArtisanEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("fallen_artisan"));

    public static final DeferredHolder<EntityType<?>, EntityType<IronHammerEntity>> IRON_HAMMER =
            ENTITIES.register("iron_hammer", () -> EntityType.Builder
                    .<IronHammerEntity>of(IronHammerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).clientTrackingRange(6).build("iron_hammer"));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownToolEntity>> THROWN_TOOL =
            ENTITIES.register("thrown_tool", () -> EntityType.Builder
                    .<ThrownToolEntity>of(ThrownToolEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F).clientTrackingRange(6).build("thrown_tool"));

    // ==== 恶震渊井 ====
    public static final DeferredHolder<EntityType<?>, EntityType<AbyssSkeletonEntity>> ABYSS_SKELETON =
            ENTITIES.register("abyss_skeleton", () -> EntityType.Builder
                    .of(AbyssSkeletonEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).clientTrackingRange(10).build("abyss_skeleton"));

    public static final DeferredHolder<EntityType<?>, EntityType<AbyssCreeperEntity>> ABYSS_CREEPER =
            ENTITIES.register("abyss_creeper", () -> EntityType.Builder
                    .of(AbyssCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F).clientTrackingRange(10).build("abyss_creeper"));

    private BeastiaryEntities() {
    }
}
