package net.mcreator.mut.trait;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface TraitEffect {

    String getTrigger();
    void apply(LivingEntity user, LivingEntity target, ItemStack stack, BlockState brokenBlock);

    record ValueRange(float min, float max) {
        public float roll(java.util.Random rand) {
            return min + rand.nextFloat() * (max - min);
        }
        public int rollInt(java.util.Random rand) {
            return Math.round(roll(rand));
        }
    }

    record EntityFilter(String id, String type) {
        public boolean matches(LivingEntity entity) {
            if (id != null) {
                ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                return entityId.toString().equals(id);
            }
            if (type != null) {
                return entity.getType().is(TagKey.create(
                        net.minecraft.core.registries.Registries.ENTITY_TYPE,
                        ResourceLocation.parse(type.substring(1))
                ));
            }
            return true;
        }
    }

    record BlockFilter(String id, String type) {
        public boolean matches(BlockState state) {
            if (id != null) {
                return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().equals(id);
            }
            if (type != null) {
                return state.is(TagKey.create(
                        net.minecraft.core.registries.Registries.BLOCK,
                        ResourceLocation.parse(type.substring(1))
                ));
            }
            return true;
        }
    }
}