package net.mcreator.mut.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MutWolfArmorLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {

    private static final Map<String, ResourceLocation> TEXTURE_MAP = new HashMap<>();
    static {
        TEXTURE_MAP.put("mut:iron_wolf_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/wolf/iron_wolf_armor.png"));
        TEXTURE_MAP.put("mut:copper_wolf_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/wolf/copper_wolf_armor.png"));
        TEXTURE_MAP.put("mut:golden_wolf_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/wolf/golden_wolf_armor.png"));
        TEXTURE_MAP.put("mut:diamond_wolf_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/wolf/diamond_wolf_armor.png"));
        TEXTURE_MAP.put("mut:netherite_wolf_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/wolf/netherite_wolf_armor.png"));
    }

    private final WolfModel<Wolf> model;

    public MutWolfArmorLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new WolfModel<>(modelSet.bakeLayer(ModelLayers.WOLF_ARMOR));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Wolf wolf,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack armor = wolf.getBodyArmorItem();
        String itemId = BuiltInRegistries.ITEM.getKey(armor.getItem()).toString();
        ResourceLocation texture = TEXTURE_MAP.get(itemId);

        if (texture == null) return;

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(wolf, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(wolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

}