package net.mcreator.mut.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MutHorseArmorLayer extends RenderLayer<Horse, HorseModel<Horse>> {
    private final HorseModel<Horse> model;

    private static final Map<String, ResourceLocation> TEXTURE_MAP = new HashMap<>();
    static {
        TEXTURE_MAP.put("mut:netherite_horse_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_horse_armor.png"));
        TEXTURE_MAP.put("mut:copper_horse_armor", ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/copper_horse_armor.png"));
    }

    public MutHorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new HorseModel<>(modelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Horse horse,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack armor = horse.getBodyArmorItem();
        if (armor.getItem() instanceof AnimalArmorItem animalArmor
                && animalArmor.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {

            poseStack.pushPose();

            String itemId = BuiltInRegistries.ITEM.getKey(armor.getItem()).toString();
            ResourceLocation texture = TEXTURE_MAP.getOrDefault(itemId, animalArmor.getTexture());

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(horse, limbSwing, limbSwingAmount, partialTick);
            this.model.setupAnim(horse, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            int color;
            if (armor.is(ItemTags.DYEABLE)) {
                color = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(armor, -6265536));
            } else {
                color = -1;
            }

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);

            poseStack.popPose();
        }
    }
}