package net.mcreator.mut.porting.ruingolem.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.ruingolem.RuinGolemBlocks;
import net.mcreator.mut.porting.ruingolem.RuinGolemEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 傀儡渲染器注册。模型沿用原版铁傀儡，贴图各自独立。
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class RuinGolemRenderers {

    private static final String TEXTURE_ROOT = "textures/entity/ruin_golem/";

    public static final ResourceLocation OBSIDIAN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, TEXTURE_ROOT + "obsidian_ruin_golem.png");
    public static final ResourceLocation CRYING_OBSIDIAN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, TEXTURE_ROOT + "crying_obsidian_ruin_golem.png");
    public static final ResourceLocation ROCK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, TEXTURE_ROOT + "rock_ruin_golem.png");

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RuinGolemEntities.OBSIDIAN_RUIN_GOLEM.get(),
                (EntityRendererProvider<IronGolem>) context -> new RuinGolemRenderer(context, OBSIDIAN_TEXTURE));
        event.registerEntityRenderer(RuinGolemEntities.CRYING_OBSIDIAN_RUIN_GOLEM.get(),
                (EntityRendererProvider<IronGolem>) context -> new RuinGolemRenderer(context, CRYING_OBSIDIAN_TEXTURE));
        event.registerEntityRenderer(RuinGolemEntities.ROCK_RUIN_GOLEM.get(),
                (EntityRendererProvider<IronGolem>) context -> new RockRuinGolemRenderer(context, ROCK_TEXTURE));
        event.registerBlockEntityRenderer(RuinGolemBlocks.ROCK_GOLEM_BLOCK_ENTITY.get(), RockGolemBlockRenderer::new);
    }

    private RuinGolemRenderers() {
    }
}
