package net.mcreator.mut.porting.armorstand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.mcreator.mut.porting.armorstand.client.model.StaticWolfModel;
import net.mcreator.mut.porting.armorstand.WolfArmorStandEntity;

import net.mcreator.mut.porting.animalarmor.client.MutAnimalArmorTextures;
import net.mcreator.mut.porting.animalarmor.client.MutAnimalArmorTrimRenderer;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/**
 * 狼铠展示架的自写铠层：复刻原版 WolfArmorLayer 的三层渲染
 * （底图 + 染色 overlay + 耐久裂纹），并内联纹饰渲染。
 *
 * <p>不复用原版层的原因：原版层的内置模型不可控（尾巴会随 ageInTicks
 * 永远摆动），且 hasArmor() 只认原版 wolf_armor 物品。这里使用
 * StaticWolfModel 保证本体与铠全部静止，且任意 CANINE 狼铠均可渲染。</p>
 */
public class WolfArmorStandArmorLayer
        extends RenderLayer<
        WolfArmorStandEntity,
        StaticWolfModel<WolfArmorStandEntity>
        > {

    private static final ResourceLocation CRACK_TEXTURES_LOW =
            ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png");
    private static final ResourceLocation CRACK_TEXTURES_MEDIUM =
            ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png");
    private static final ResourceLocation CRACK_TEXTURES_HIGH =
            ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png");

    private final StaticWolfModel<WolfArmorStandEntity> armorModel;

    public WolfArmorStandArmorLayer(
            RenderLayerParent<
                    WolfArmorStandEntity,
                    StaticWolfModel<WolfArmorStandEntity>
                    > parent,
            StaticWolfModel<WolfArmorStandEntity> armorModel
    ) {
        super(parent);
        this.armorModel = armorModel;
    }

    public WolfArmorStandArmorLayer(
            RenderLayerParent<
                    WolfArmorStandEntity,
                    StaticWolfModel<WolfArmorStandEntity>
                    > parent,
            net.minecraft.client.model.geom.EntityModelSet modelSet
    ) {
        this(parent, new StaticWolfModel<>(modelSet.bakeLayer(ModelLayers.WOLF_ARMOR)));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            WolfArmorStandEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack armor = entity.getBodyArmorItem();

        if (!(armor.getItem() instanceof AnimalArmorItem animalArmorItem)
                || animalArmorItem.getBodyType() != AnimalArmorItem.BodyType.CANINE) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.armorModel);
        this.armorModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.armorModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // 1. 狼铠底图：模组狼铠按约定取 textures/entity/wolf/<id>.png，缺失时回退原版贴图
        ResourceLocation baseTexture = MutAnimalArmorTextures.resolve(armor, animalArmorItem);
        VertexConsumer baseConsumer = buffer.getBuffer(
                RenderType.entityCutoutNoCull(baseTexture)
        );
        this.armorModel.renderToBuffer(poseStack, baseConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        // 附魔光效（与狼身上的渲染保持一致）
        if (armor.isEnchanted()) {
            this.armorModel.renderToBuffer(
                    poseStack,
                    buffer.getBuffer(RenderType.entityGlintDirect()),
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );
        }

        // 2. 染色 overlay（未染色 alpha==0 时原版直接跳过）
        this.renderColoredOverlay(poseStack, buffer, packedLight, armor, animalArmorItem);

        // 3. 耐久裂纹（展示架铠不掉耐久，通常为 NONE）
        this.renderCracks(poseStack, buffer, packedLight, armor);

        // 4. 纹饰
        this.renderTrim(poseStack, buffer, packedLight, armor);
    }

    private void renderColoredOverlay(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ItemStack armor,
            AnimalArmorItem animalArmorItem
    ) {
        if (!armor.is(ItemTags.DYEABLE)) {
            return;
        }

        int color = DyedItemColor.getOrDefault(armor, 0);
        if (FastColor.ARGB32.alpha(color) == 0) {
            return;
        }

        ResourceLocation overlay = animalArmorItem.getOverlayTexture();
        if (overlay == null) {
            return;
        }

        this.armorModel.renderToBuffer(
                poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCull(overlay)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                FastColor.ARGB32.opaque(color)
        );
    }

    private void renderCracks(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ItemStack armor
    ) {
        Crackiness.Level level = Crackiness.WOLF_ARMOR.byDamage(armor);

        ResourceLocation texture = switch (level) {
            case LOW -> CRACK_TEXTURES_LOW;
            case MEDIUM -> CRACK_TEXTURES_MEDIUM;
            case HIGH -> CRACK_TEXTURES_HIGH;
            default -> null;
        };

        if (texture == null) {
            return;
        }

        this.armorModel.renderToBuffer(
                poseStack,
                buffer.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }

    private void renderTrim(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ItemStack armor
    ) {
        // 纹饰层走移植后的动物铠纹饰渲染：模板白名单 + 无发光 + 贴图缺失时静默跳过
        MutAnimalArmorTrimRenderer.renderTrimLayer(
                this.armorModel,
                poseStack,
                buffer,
                packedLight,
                armor,
                "wolf_armor"
        );
    }
}