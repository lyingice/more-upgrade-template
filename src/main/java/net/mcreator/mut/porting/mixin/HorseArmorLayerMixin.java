package net.mcreator.mut.porting.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.mcreator.mut.porting.animalarmor.client.MutAnimalArmorTrimRenderer;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 马铠纹饰层（不含发光）。 */
@Mixin(HorseArmorLayer.class)
public abstract class HorseArmorLayerMixin {

    @Shadow
    @Final
    private HorseModel<Horse> model;

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "ILnet/minecraft/world/entity/animal/horse/Horse;" +
                    "FFFFFF)V",
            at = @At("RETURN")
    )
    private void mut$renderHorseArmorTrim(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Horse horse,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        ItemStack armor = horse.getBodyArmorItem();
        if (!(armor.getItem() instanceof AnimalArmorItem animalArmorItem)) {
            return;
        }
        if (animalArmorItem.getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) {
            return;
        }

        MutAnimalArmorTrimRenderer.renderTrimLayer(
                this.model, poseStack, buffer, packedLight, armor, "horse_armor"
        );
    }
}
