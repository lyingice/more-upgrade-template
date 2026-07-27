package net.mcreator.mut.affix.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DurabilityRepairEffect implements AffixEffect {
    private final String trigger;
    private final int perDurability, saturationPerPoint;

    public DurabilityRepairEffect(String trigger, int perDurability, int saturationPerPoint) {
        this.trigger = trigger; this.perDurability = perDurability; this.saturationPerPoint = saturationPerPoint;
    }
    @Override public String getTrigger() { return trigger; }
    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {}
    public int getPerDurability() { return perDurability; }
    public int getSaturationPerPoint() { return saturationPerPoint; }
    public int getMaxRepair(int level) { return Math.round(level * 0.5F); }
}