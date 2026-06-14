package net.mcreator.mut.mixin;

import net.mcreator.mut.api.IDurableFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void onEat(Level level, ItemStack stack, FoodProperties foodProps,
                       CallbackInfoReturnable<ItemStack> cir) {

        if (!(stack.getItem() instanceof IDurableFoodItem durableFood)) {
            return;
        }

        durableFood.setContext(stack, (LivingEntity) (Object) this, level);

        if (durableFood.handleDurabilityConsumption()) {
            LivingEntity self = (LivingEntity) (Object) this;

            level.playSound(null, self.getX(), self.getY(), self.getZ(),
                    self.getEatingSound(stack), SoundSource.NEUTRAL,
                    1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

            for (var effect : foodProps.effects()) {
                if (!level.isClientSide() && (effect.probability() >= 1.0F
                        || level.random.nextFloat() < effect.probability())) {
                    self.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect.effect()));
                }
            }

            if (self instanceof Player player) {
                player.getFoodData().eat(foodProps.nutrition(), foodProps.saturation());
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }

            self.gameEvent(GameEvent.EAT);

            if (durableFood.isItemBroken()) {
                stack.consume(1, self);
            }

            cir.setReturnValue(stack);
        }
    }
}