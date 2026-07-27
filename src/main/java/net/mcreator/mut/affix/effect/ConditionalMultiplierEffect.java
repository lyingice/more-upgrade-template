package net.mcreator.mut.affix.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ConditionalMultiplierEffect implements AffixEffect {
    private final String trigger, condition;
    private final double perLevel;

    public ConditionalMultiplierEffect(String trigger, String condition, double perLevel) {
        this.trigger = trigger; this.condition = condition; this.perLevel = perLevel;
    }
    @Override public String getTrigger() { return trigger; }
    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {}

    public double getPerLevel() { return perLevel; }
    public String getCondition() { return condition; }

    public boolean isActive(LivingEntity entity) {
        return switch (condition) {
            case "water"     -> entity.isInWater();
            case "rain"      -> entity.level().isRaining() && entity.level().canSeeSky(entity.blockPosition());
            case "sprinting" -> entity.isSprinting();
            case "falling"   -> entity.fallDistance > 1.5F;
            case "midair"    -> !entity.onGround() && entity.fallDistance > 0.0F;
            default -> true;
        };
    }
}