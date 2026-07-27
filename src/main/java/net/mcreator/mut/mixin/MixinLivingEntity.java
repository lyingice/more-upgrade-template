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

/**
 * 这是一个通过@Inject注解注入的方法，用于处理食物被吃掉的行为
 * 该方法在原始的eat方法执行前被调用，并可能取消原始方法的执行
 *
 * @param level 当前世界对象
 * @param stack 被食用的物品堆栈
 * @param foodProps 食物属性，包含营养值、饱和度和效果等信息
 * @param cir 用于返回修改后的结果或取消操作
 */
    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void onEat(Level level, ItemStack stack, FoodProperties foodProps,
                       CallbackInfoReturnable<ItemStack> cir) {

    // 检查物品是否实现了IDurableFoodItem接口，如果不实现则直接返回
        if (!(stack.getItem() instanceof IDurableFoodItem durableFood)) {
            return;
        }

    // 设置食物的上下文信息，包括物品、食用者和世界
        durableFood.setContext(stack, (LivingEntity) (Object) this, level);

    // 处理耐久度消耗
        if (durableFood.handleDurabilityConsumption()) {
        // 获取当前实体的引用
            LivingEntity self = (LivingEntity) (Object) this;

        // 播放进食音效
            level.playSound(null, self.getX(), self.getY(), self.getZ(),
                    self.getEatingSound(stack), SoundSource.NEUTRAL,
                    1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

        // 处理食物效果
            for (var effect : foodProps.effects()) {
            // 如果不是客户端世界，并且效果概率满足条件，则添加效果
                if (!level.isClientSide() && (effect.probability() >= 1.0F
                        || level.random.nextFloat() < effect.probability())) {
                    self.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect.effect()));
                }
            }

        // 如果实体是玩家，则增加饱食度和饱和度，并使用统计
            if (self instanceof Player player) {
                player.getFoodData().eat(foodProps.nutrition(), foodProps.saturation());
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }

        // 触发进食游戏事件
            self.gameEvent(GameEvent.EAT);

        // 如果物品已损坏，则消耗一个物品
            if (durableFood.isItemBroken()) {
                stack.consume(1, self);
            }

        // 返回修改后的物品堆栈
            cir.setReturnValue(stack);
        }
    }
}