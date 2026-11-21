package net.mcreator.mut.porting.smithing;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimPattern;

/**
 * 移植纹饰的数据键（数据文件位于 {@code data/mut/trim_pattern/}）。
 */
public final class MutTrimPatternKeys {
    public static final ResourceKey<TrimPattern> ARROWHEAD =
            create("arrowhead");

    public static final ResourceKey<TrimPattern> NETHERITE =
            create("netherite");

    private static ResourceKey<TrimPattern> create(String name) {
        return ResourceKey.create(
                Registries.TRIM_PATTERN,
                ResourceLocation.fromNamespaceAndPath("mut", name)
        );
    }

    private MutTrimPatternKeys() {
    }
}
