package net.mcreator.mut.trait.effect;

import net.mcreator.mut.trait.TraitEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class IgniteTarget implements TraitEffect {
    private final String trigger;
    private final ValueRange seconds;

    public IgniteTarget(String trigger, ValueRange seconds) {
        this.trigger = trigger;
        this.seconds = seconds;
    }

    @Override public String getTrigger() { return trigger; }
    @Override public void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState broken) {
        if (target != null) {
            java.util.Random r = new java.util.Random();
            target.setRemainingFireTicks(seconds.rollInt(r) * 20);
        }
    }
}