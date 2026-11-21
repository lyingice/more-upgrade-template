package net.mcreator.mut.porting.smithing.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 合并旁挂 override 的 {@link ItemOverrides}（移植自 TCR 的 TcrItemOverrides）。
 */
public class MutItemOverrides extends ItemOverrides {

    /** 单条旁挂 override：谓词列表 + 目标模型。 */
    public record BakedEntry(List<ItemOverride.Predicate> predicates, @Nullable BakedModel model) {
    }

    private final BakedEntry[] entries;
    private final ItemOverrides fallback;

    public MutItemOverrides(BakedEntry[] entries, ItemOverrides fallback) {
        super();
        this.entries = entries;
        this.fallback = fallback;
    }

    @Override
    @Nullable
    public BakedModel resolve(
            BakedModel originalModel,
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity,
            int seed
    ) {
        for (int i = entries.length - 1; i >= 0; i--) {
            BakedEntry entry = entries[i];
            if (matchesAll(stack, entry.predicates(), level, entity, seed)) {
                BakedModel model = entry.model();
                return model != null ? model : originalModel;
            }
        }

        return fallback.resolve(originalModel, stack, level, entity, seed);
    }

    private boolean matchesAll(
            ItemStack stack,
            List<ItemOverride.Predicate> predicates,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity,
            int seed
    ) {
        for (ItemOverride.Predicate predicate : predicates) {
            ResourceLocation propertyId = predicate.getProperty();
            ItemPropertyFunction function = ItemProperties.getProperty(stack, propertyId);

            // 谓词未注册：与原版语义一致，视为不命中。
            if (function == null) {
                return false;
            }

            float actual = function.call(stack, level, entity, seed);
            if (actual < predicate.getValue()) {
                return false;
            }
        }
        return true;
    }
}
