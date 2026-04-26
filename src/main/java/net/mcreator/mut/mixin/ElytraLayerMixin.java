package net.mcreator.mut.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.mcreator.mut.item.IronElytraItem;
import net.mcreator.mut.item.GoldenElytraItem;
import net.mcreator.mut.item.DiamondElytraItem;
import net.mcreator.mut.item.NetheriteElytraItem;
import net.mcreator.mut.item.DragonChestplateElytraItem;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {
    
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof IronElytraItem ||
            stack.getItem() instanceof GoldenElytraItem ||
            stack.getItem() instanceof DiamondElytraItem ||
            stack.getItem() instanceof NetheriteElytraItem ||
            stack.getItem() instanceof DragonChestplateElytraItem) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "getElytraTexture", at = @At("HEAD"), cancellable = true)
    private void getElytraTexture(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        ItemStack chestStack = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        
        if (chestStack.getItem() instanceof IronElytraItem) {
            cir.setReturnValue(ResourceLocation.parse("mut:textures/entities/iron_elytra.png"));
        } else if (chestStack.getItem() instanceof GoldenElytraItem) {
            cir.setReturnValue(ResourceLocation.parse("mut:textures/entities/golden_elytra.png"));
        } else if (chestStack.getItem() instanceof DiamondElytraItem) {
            cir.setReturnValue(ResourceLocation.parse("mut:textures/entities/diamond_elytra.png"));
        } else if (chestStack.getItem() instanceof NetheriteElytraItem) {
            cir.setReturnValue(ResourceLocation.parse("mut:textures/entities/netherite_elytra.png"));
        } else if (chestStack.getItem() instanceof DragonChestplateElytraItem) {
            cir.setReturnValue(ResourceLocation.parse("minecraft:textures/entity/elytra.png"));
        }
    }
}