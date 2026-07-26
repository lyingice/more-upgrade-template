package net.mcreator.mut.trait.effect;

import net.mcreator.mut.trait.TraitEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DamageTarget implements TraitEffect {
    private final String trigger;
    private final Holder<DamageType> damageType;
    private final ValueRange amount;
    public DamageTarget(String trigger, String damageTypeId, ValueRange amount) {
        this.trigger = trigger;
        Registry<DamageType> registry = (Registry<DamageType>) BuiltInRegistries.REGISTRY.get((ResourceKey) Registries.DAMAGE_TYPE);
        this.damageType = registry.getHolderOrThrow(
                ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(damageTypeId))
        );
        this.amount = amount;
    }

    @Override public String getTrigger() { return trigger; }
    @Override public void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState broken) {
        if (target != null) {
            java.util.Random r = new java.util.Random();
            target.hurt(new DamageSource(damageType, user), amount.roll(r));
        }
    }
}