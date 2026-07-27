package net.mcreator.mut.affix.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DamageMultiplierEffect implements AffixEffect {
    private final String trigger;
    private final double perLevel;
    private final boolean rangedOnly;

    public DamageMultiplierEffect(String trigger, double perLevel) {
        this(trigger, perLevel, false);
    }

    public DamageMultiplierEffect(String trigger, double perLevel, boolean rangedOnly) {
        this.trigger = trigger;
        this.perLevel = perLevel;
        this.rangedOnly = rangedOnly;
    }

    @Override public String getTrigger() { return trigger; }
    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {}

    public double getPerLevel() { return perLevel; }
    public boolean isRangedOnly() { return rangedOnly; }
}