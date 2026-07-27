package net.mcreator.mut.affix.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public interface AffixEffect {
    String getTrigger();
    void apply(LivingEntity user, @Nullable LivingEntity target, ItemStack stack, int level,
               @Nullable BlockState brokenBlock);

    record ValueRange(float min, float max) {
        public float roll(java.util.Random rand) { return min + rand.nextFloat() * (max - min); }
        public int rollInt(java.util.Random rand) { return Math.round(roll(rand)); }
    }
}