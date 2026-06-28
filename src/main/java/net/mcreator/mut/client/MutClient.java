package net.mcreator.mut.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.client.model.ModelCustomModel;
import net.mcreator.mut.client.model.ModelMutTridentModel;
import net.mcreator.mut.client.particle.GraySonicBoomParticle;
import net.mcreator.mut.client.renderer.EliteAborigineZombieRenderer;
import net.mcreator.mut.client.renderer.MutThrownTridentRenderer;
import net.mcreator.mut.client.renderer.MutTrialSpawnerRenderer;
import net.mcreator.mut.client.renderer.SigilForgeVaultRenderer;
import net.mcreator.mut.client.renderer.layer.*;
import net.mcreator.mut.init.MutModBlockEntities;
import net.mcreator.mut.init.MutModEntities;
import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutModParticles;
import net.mcreator.mut.item.armor.IChargedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class MutClient {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelMutTridentModel.LAYER_LOCATION, ModelMutTridentModel::createBodyLayer);
        event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
        event.registerLayerDefinition(
                new ModelLayerLocation(
                        ResourceLocation.fromNamespaceAndPath("mut", "charged_armor"),
                        "main"
                ),
                () -> LayerDefinition.create(
                        HumanoidModel.createMesh(new CubeDeformation(1.5F), 0.0F),
                        128, 128  // ← 从 64,64 改成 128,128
                )
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MutModItems.MUT_THROWN_TRIDENT.get(), MutThrownTridentRenderer::new);
        event.registerBlockEntityRenderer(
                MutModBlockEntities.SIGIL_FORGE_VAULT_COMMON.get(),
                SigilForgeVaultRenderer::new
        );
        event.registerBlockEntityRenderer(
                MutModBlockEntities.SIGIL_FORGE_VAULT_UNIQUE.get(),
                SigilForgeVaultRenderer::new
        );
        event.registerBlockEntityRenderer(
                MutModBlockEntities.SIGIL_FORGE_VAULT_BOSS.get(),
                SigilForgeVaultRenderer::new
        );
        event.registerEntityRenderer(
                MutModEntities.SIGIL_FORGE_ZOMBIE_BOSS.get(),
                EliteAborigineZombieRenderer::new
        );
        event.registerBlockEntityRenderer(
                MutModBlockEntities.SIGIL_FORGE_TRIAL_SPAWNER.get(),
                MutTrialSpawnerRenderer::new
        );
        event.registerBlockEntityRenderer(
                MutModBlockEntities.SIGIL_FORGE_UNIQUE_TRIAL_SPAWNER.get(),
                MutTrialSpawnerRenderer::new
        );
        event.registerBlockEntityRenderer(
                MutModBlockEntities.SIGIL_FORGE_BOSS_TRIAL_SPAWNER.get(),
                MutTrialSpawnerRenderer::new
        );
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        var wolfRenderer = event.getRenderer(EntityType.WOLF);
        if (wolfRenderer instanceof WolfRenderer wr) {
            wr.addLayer(new MutWolfArmorLayer(wr, event.getEntityModels()));
        }
        var horseRenderer = event.getRenderer(EntityType.HORSE);
        if (horseRenderer instanceof HorseRenderer hr) {
            hr.addLayer(new MutHorseArmorLayer(hr, event.getEntityModels()));
        }
        // 玩家（默认皮肤）
        var defaultSkin = event.getSkin(PlayerSkin.Model.WIDE);
        if (defaultSkin instanceof PlayerRenderer pr) {
            pr.addLayer(new ChargedArmorLayer<>(pr, event.getEntityModels()));
        }
        event.getSkins().forEach(skin -> {
                    PlayerRenderer renderer = event.getSkin(skin);
                    if (renderer != null) {
                        renderer.addLayer(new CustomElytraLayer(renderer, event.getEntityModels()));
                    }
                });

        // 玩家（细手臂皮肤）
        var slimSkin = event.getSkin(PlayerSkin.Model.SLIM);
        if (slimSkin instanceof PlayerRenderer pr) {
            pr.addLayer(new ChargedArmorLayer<>(pr, event.getEntityModels()));
        }
        event.getSkins().forEach(skin -> {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new CustomElytraLayer(renderer, event.getEntityModels()));
            }
        });
        // 盔甲架
        var armorStandRenderer = event.getRenderer(EntityType.ARMOR_STAND);
        if (armorStandRenderer instanceof ArmorStandRenderer asr) {
            asr.addLayer(new ChargedArmorLayer<>(asr, event.getEntityModels()));
            asr.addLayer(new CustomElytraLayer(asr, event.getEntityModels()));
        }
    }
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                MutModParticles.GRAY_SONIC_BOOM.get(),
                GraySonicBoomParticle.Provider::new
        );
    }

}