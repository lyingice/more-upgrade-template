package net.mcreator.mut.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfArmorMixin {

    private static final TagKey<Item> WOLF_ARMOR_TAG =
            TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("mut", "wolf_armor"));

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (!wolf.isTame() || !wolf.isOwnedBy(player)) return;

        ItemStack heldStack = player.getItemInHand(hand);
        if (!heldStack.is(WOLF_ARMOR_TAG)) return;

        ItemStack wolfArmor = wolf.getBodyArmorItem();

        if (wolfArmor.isEmpty()) {
            ItemStack copy = heldStack.copy();
            copy.setCount(1);
            wolf.setBodyArmorItem(copy);
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (!ItemStack.isSameItem(wolfArmor, heldStack)) {
            wolf.spawnAtLocation(wolfArmor);
            ItemStack copy = heldStack.copy();
            copy.setCount(1);
            wolf.setBodyArmorItem(copy);
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}