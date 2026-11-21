package net.mcreator.mut.porting.animalarmor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.animalarmor.MutAnimalArmorTrim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

/**
 * 动物铠（马铠 / 狼铠）纹饰层的渲染。
 *
 * <p>与原项目不同：<b>不再支持发光纹饰</b>，纹饰层始终使用铠本体同样的光照
 * （原实现的 {@code GLOW_TRIM} 全亮度分支已移除）。</p>
 */
public final class MutAnimalArmorTrimRenderer {

    private MutAnimalArmorTrimRenderer() {
    }

    /**
     * 用动物铠模型渲染一层纹饰。
     *
     * @param animalType {@code horse_armor} 或 {@code wolf_armor}
     */
    public static void renderTrimLayer(
            Model model,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ItemStack armor,
            String animalType
    ) {
        ArmorTrim trim = armor.get(DataComponents.TRIM);
        if (trim == null) {
            return;
        }

        // 限制：只有白名单模板产生的纹饰才允许出现在动物铠上
        if (!isAllowedTemplate(trim)) {
            return;
        }

        String patternPath = trim.pattern().value().assetId().getPath();
        String materialName = trim.material().value().assetName();

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                MutMod.MODID,
                "trims/models/" + animalType + "/" + patternPath + "_" + materialName
        );

        TextureAtlas atlas = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(Sheets.ARMOR_TRIMS_SHEET);

        TextureAtlasSprite sprite = atlas.getSprite(texture);

        // 该图案没有对应的动物铠贴图时静默跳过，避免出现紫黑格
        if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
            return;
        }

        VertexConsumer consumer = sprite.wrap(
                buffer.getBuffer(
                        Sheets.armorTrimsSheet(trim.pattern().value().decal())
                )
        );

        // 不使用发光：直接沿用铠本体的光照
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    private static boolean isAllowedTemplate(ArmorTrim trim) {
        Item templateItem = trim.pattern().value().templateItem().value();
        return new ItemStack(templateItem).is(MutAnimalArmorTrim.ALLOWED_TEMPLATES);
    }
}
