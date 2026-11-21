package net.mcreator.mut.beastiary.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.beastiary.*;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 新生物渲染器。
 * <p>
 * 全部沿用原版模型，只换贴图（贴图目前是原版重上色的占位图）。
 * 最终 BOSS「模板之神」用玩家模型 + 一张 64×64 占位皮肤。
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class BeastiaryRenderers {

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "textures/entity/" + name + ".png");
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpiderQueenRenderer.LAYER, SpiderQueenModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BeastiaryEntities.TUSKED_WARLORD.get(),
                (EntityRendererProvider<TuskedWarlordEntity>) ctx -> new PiglinLikeRenderer<>(ctx, tex("tusked_warlord")));
        event.registerEntityRenderer(BeastiaryEntities.PIGLIN_MERCENARY_RANGED.get(),
                (EntityRendererProvider<RangedPiglinMercenaryEntity>) ctx -> new PiglinLikeRenderer<>(ctx, tex("piglin_mercenary_ranged")));
        event.registerEntityRenderer(BeastiaryEntities.PIGLIN_MERCENARY_DEFENDER.get(),
                (EntityRendererProvider<DefenderPiglinMercenaryEntity>) ctx -> new PiglinLikeRenderer<>(ctx, tex("piglin_mercenary_defender")));

        event.registerEntityRenderer(BeastiaryEntities.SPIDER_QUEEN.get(), SpiderQueenRenderer::new);
        event.registerEntityRenderer(BeastiaryEntities.DARK_SPIDER.get(),
                (EntityRendererProvider<DarkSpiderEntity>) ctx -> new CustomSpiderRenderer<>(ctx, tex("dark_spider")));
        event.registerEntityRenderer(BeastiaryEntities.DEEP_LEAPING_SPIDER.get(),
                (EntityRendererProvider<DeepLeapingSpiderEntity>) ctx -> new CustomSpiderRenderer<>(ctx, tex("deep_leaping_spider")));

        event.registerEntityRenderer(BeastiaryEntities.FALLEN_TEMPLATE_SAGE.get(),
                (EntityRendererProvider<FallenTemplateSageEntity>) ctx -> new ZombieLikeRenderer<>(ctx, tex("fallen_template_sage")));
        event.registerEntityRenderer(BeastiaryEntities.FALLEN_ARTISAN.get(),
                (EntityRendererProvider<FallenArtisanEntity>) ctx -> new ZombieLikeRenderer<>(ctx, tex("fallen_artisan")));

        event.registerEntityRenderer(BeastiaryEntities.TEMPLATE_GOD_REMAINS.get(),
                (EntityRendererProvider<TemplateGodRemainsEntity>) ctx -> new GodRemainsRenderer(ctx, tex("template_god")));

        event.registerEntityRenderer(BeastiaryEntities.ABYSS_SKELETON.get(),
                (EntityRendererProvider<AbyssSkeletonEntity>) ctx -> new SkeletonLikeRenderer(ctx, tex("abyss_skeleton")));
        event.registerEntityRenderer(BeastiaryEntities.ABYSS_CREEPER.get(),
                (EntityRendererProvider<AbyssCreeperEntity>) ctx -> new AbyssCreeperRenderer(ctx, tex("abyss_creeper")));

        event.registerEntityRenderer(BeastiaryEntities.TUSK_JAVELIN.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BeastiaryEntities.IRON_HAMMER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BeastiaryEntities.THROWN_TOOL.get(), ThrownItemRenderer::new);
    }

    /** 猪灵类（军阀 / 雇佣兵变体） */
    private static class PiglinLikeRenderer<T extends Mob> extends HumanoidMobRenderer<T, PiglinModel<T>> {
        private final ResourceLocation texture;

        PiglinLikeRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture) {
            super(ctx, new PiglinModel<>(ctx.bakeLayer(ModelLayers.PIGLIN)), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return texture;
        }
    }

    /** 蜘蛛类 */
    private static class CustomSpiderRenderer<T extends net.minecraft.world.entity.monster.Spider>
            extends SpiderRenderer<T> {
        private final ResourceLocation texture;

        CustomSpiderRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture) {
            super(ctx);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return texture;
        }
    }

    /** 僵尸模型的人形（圣匠 / 工匠） */
    private static class ZombieLikeRenderer<T extends Mob> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
        private final ResourceLocation texture;

        ZombieLikeRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture) {
            super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return texture;
        }
    }

    /** 骷髅类（坠骸） */
    private static class SkeletonLikeRenderer extends HumanoidMobRenderer<AbyssSkeletonEntity, SkeletonModel<AbyssSkeletonEntity>> {
        private final ResourceLocation texture;

        SkeletonLikeRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture) {
            super(ctx, new SkeletonModel<>(ctx.bakeLayer(ModelLayers.SKELETON)), 0.5F);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(AbyssSkeletonEntity entity) {
            return texture;
        }
    }

    /** 最终 BOSS：玩家模型 */
    private static class GodRemainsRenderer extends HumanoidMobRenderer<TemplateGodRemainsEntity, PlayerModel<TemplateGodRemainsEntity>> {
        private final ResourceLocation texture;

        GodRemainsRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture) {
            super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(TemplateGodRemainsEntity entity) {
            return texture;
        }
    }

    /** 苦力怕 */
    private static class AbyssCreeperRenderer extends MobRenderer<AbyssCreeperEntity, CreeperModel<AbyssCreeperEntity>> {
        private final ResourceLocation texture;

        AbyssCreeperRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture) {
            super(ctx, new CreeperModel<>(ctx.bakeLayer(ModelLayers.CREEPER)), 0.5F);
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation(AbyssCreeperEntity entity) {
            return texture;
        }
    }

    private BeastiaryRenderers() {
    }
}
