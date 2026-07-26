package net.mcreator.mut.trait.effect;

import net.mcreator.mut.trait.TraitEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class KnockbackTarget implements TraitEffect {
    private final String trigger;
    private final float strength;

    public KnockbackTarget(String trigger, float strength) {
        this.trigger = trigger;
        this.strength = strength;
    }

    @Override public String getTrigger() { return trigger; }
    @Override public void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState broken) {
        if (target != null && user != null) {
            target.knockback(strength,
                    Math.sin(user.getYRot() * Math.PI / 180),
                    -Math.cos(user.getYRot() * Math.PI / 180));
        }
    }
}