package net.mcreator.mut.porting.armorstand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.mcreator.mut.porting.animalarmor.client.MutAnimalArmorTextures;
import net.mcreator.mut.porting.animalarmor.client.MutAnimalArmorTrimRenderer;
import net.mcreator.mut.porting.armorstand.HorseArmorStandEntity;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/**
 * 马铠展示架的铠层。
 *
 * <p>不复用原版 {@code HorseArmorLayer}：原版层用 {@link AnimalArmorItem#getTexture()} 取贴图，
 * 模组马铠会显示成原版外观。这里改用 {@link MutAnimalArmorTextures} 解析模组贴图，
 * 并保持一致地渲染染色 tint、附魔光效与纹饰。</p>
 */
public class HorseArmorStandArmorLayer
        extends RenderLayer<HorseArmorStandEntity, HorseModel<HorseArmorStandEntity>> {

    /** 原版 HorseArmorLayer 对可染色马铠使用的默认颜色。 */
    private static final int DEFAULT_DYE_COLOR = -6265536;

    private final HorseModel<HorseArmorStandEntity> armorModel;

    public HorseArmorStandArmorLayer(
            RenderLayerParent<HorseArmorStandEntity, HorseModel<HorseArmorStandEntity>> parent,
            EntityModelSet modelSet
    ) {
        super(parent);
        this.armorModel = new HorseModel<>(modelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            HorseArmorStandEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack armor = entity.getBodyArmorItem();

        if (!(armor.getItem() instanceof AnimalArmorItem animalArmorItem)
                || animalArmorItem.getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.armorModel);
        this.armorModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.armorModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // 底图：模组马铠按约定取 textures/entity/horse/armor/<id>.png
        ResourceLocation texture = MutAnimalArmorTextures.resolve(armor, animalArmorItem);

        int color = armor.is(ItemTags.DYEABLE)
                ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(armor, DEFAULT_DYE_COLOR))
                : -1;

        this.armorModel.renderToBuffer(
                poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCull(texture)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                color
        );

        // 附魔光效
        if (armor.isEnchanted()) {
            this.armorModel.renderToBuffer(
                    poseStack,
                    buffer.getBuffer(RenderType.entityGlintDirect()),
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );
        }

        // 纹饰层：模板白名单 + 无发光 + 贴图缺失静默跳过
        MutAnimalArmorTrimRenderer.renderTrimLayer(
                this.armorModel,
                poseStack,
                buffer,
                packedLight,
                armor,
                "horse_armor"
        );
    }
}
