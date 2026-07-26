package net.mcreator.mut.trait.effect;

import net.mcreator.mut.trait.TraitEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RepairOnKillEntity implements TraitEffect {
    private final String trigger;
    private final EntityFilter filter;
    private final int amount;

    public RepairOnKillEntity(String trigger, EntityFilter filter, int amount) {
        this.trigger = trigger;
        this.filter = filter;
        this.amount = amount;
    }

    @Override public String getTrigger() { return trigger; }
    @Override public void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState broken) {
        if (target != null && !target.isAlive() && stack.isDamaged() && filter.matches(target)) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - amount));
        }
    }
}