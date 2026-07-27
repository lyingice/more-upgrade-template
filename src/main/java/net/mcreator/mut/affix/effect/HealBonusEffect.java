package net.mcreator.mut.affix.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class HealBonusEffect implements AffixEffect {
    private final String trigger;
    private final double perLevel;
    private final String condition;

    public HealBonusEffect(String trigger, double perLevel) {
        this(trigger, perLevel, null);
    }

    public HealBonusEffect(String trigger, double perLevel, String condition) {
        this.trigger = trigger;
        this.perLevel = perLevel;
        this.condition = condition;
    }

    @Override public String getTrigger() { return trigger; }
    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {}

    public double getPerLevel() { return perLevel; }
    public String getCondition() { return condition; }

    public boolean isConditionMet(LivingEntity entity) {
        if (condition == null) return true;
        return switch (condition) {
            case "satiated" -> entity instanceof Player player
                    && player.getFoodData().getFoodLevel() >= 18
                    && player.getFoodData().getSaturationLevel() > 0;
            default -> true;
        };
    }
}