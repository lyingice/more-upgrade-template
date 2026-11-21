package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** 新生物属性注册。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class BeastiaryAttributes {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(BeastiaryEntities.TUSKED_WARLORD.get(), TuskedWarlordEntity.createAttributes().build());
        event.put(BeastiaryEntities.PIGLIN_MERCENARY_RANGED.get(), RangedPiglinMercenaryEntity.createAttributes().build());
        event.put(BeastiaryEntities.PIGLIN_MERCENARY_DEFENDER.get(), DefenderPiglinMercenaryEntity.createAttributes().build());
        event.put(BeastiaryEntities.SPIDER_QUEEN.get(), SpiderQueenEntity.createAttributes().build());
        event.put(BeastiaryEntities.DARK_SPIDER.get(), DarkSpiderEntity.createAttributes().build());
        event.put(BeastiaryEntities.DEEP_LEAPING_SPIDER.get(), DeepLeapingSpiderEntity.createAttributes().build());
        event.put(BeastiaryEntities.FALLEN_TEMPLATE_SAGE.get(), FallenTemplateSageEntity.createAttributes().build());
        event.put(BeastiaryEntities.TEMPLATE_GOD_REMAINS.get(), TemplateGodRemainsEntity.createAttributes().build());
        event.put(BeastiaryEntities.FALLEN_ARTISAN.get(), FallenArtisanEntity.createAttributes().build());
        event.put(BeastiaryEntities.ABYSS_SKELETON.get(), AbyssSkeletonEntity.createAttributes().build());
        event.put(BeastiaryEntities.ABYSS_CREEPER.get(), AbyssCreeperEntity.createAttributes().build());
    }

    private BeastiaryAttributes() {
    }
}
