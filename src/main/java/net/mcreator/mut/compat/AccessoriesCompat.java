package net.mcreator.mut.compat;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Accessories 饰品槽读取工具。
 * <p>
 * 本类直接引用 Accessories 的类型，因此只在 accessories 模组存在时才会被调用 / 类加载：
 * 调用方（TraitEventHandler）先用 {@code ModList.get().isLoaded("accessories")} 守卫，
 * 而 JVM 对常量池中的类引用是惰性解析的，所以 accessories 缺席时本类不会被加载，
 * 服务端也不会出现 NoClassDefFoundError。
 */
public final class AccessoriesCompat {

    /**
     * 收集实体当前装备的所有非空饰品（功能槽，不含 cosmetic 外观槽）。
     * 没有 Accessories capability 的实体（或空容器）返回空列表。
     */
    public static List<ItemStack> getEquippedStacks(LivingEntity entity) {
        Optional<AccessoriesCapability> capability = AccessoriesCapability.getOptionally(entity);
        if (capability.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemStack> stacks = new ArrayList<>();
        for (AccessoriesContainer container : capability.get().getContainers().values()) {
            int size = container.getSize();
            for (int i = 0; i < size; i++) {
                ItemStack stack = container.getAccessories().getItem(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
        }
        return stacks;
    }

    private AccessoriesCompat() {
    }
}
