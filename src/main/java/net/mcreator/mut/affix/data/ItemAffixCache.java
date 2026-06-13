package net.mcreator.mut.affix.data;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物品→可用词缀缓存 - 根据 item_affix_bindings.json 动态计算
 */
public class ItemAffixCache {

    // item ID -> List<Affix> 缓存
    private final Map<String, List<Affix>> itemAffixCache = new HashMap<>();

    // tag key -> List<Affix> 缓存
    private final Map<String, List<Affix>> tagAffixCache = new HashMap<>();

    // 所有词缀的列表（作为回退）
    private List<Affix> allAffixes;

    // 缓存是否有效
    private boolean valid = false;

    /**
     * 获取指定物品可用的词缀池
     */
    public List<Affix> getAffixesForItem(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        ensureValid();

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        // 1. 精确匹配 item
        List<Affix> exact = itemAffixCache.get(itemId);
        if (exact != null) return exact;

        // 2. 匹配 tag
        Set<Affix> matched = new LinkedHashSet<>();
        for (var entry : tagAffixCache.entrySet()) {
            TagKey<Item> tagKey = ItemTags.create(ResourceLocation.parse(entry.getKey()));
            if (stack.is(tagKey)) {
                matched.addAll(entry.getValue());
            }
        }

        if (!matched.isEmpty()) {
            return new ArrayList<>(matched);
        }

        // 3. 回退到所有词缀
        return getAllAffixes();
    }

    /**
     * 使缓存失效（配置重载时调用）
     */
    public void invalidate() {
        valid = false;
        itemAffixCache.clear();
        tagAffixCache.clear();
        allAffixes = null;
    }

    /**
     * 获取所有词缀（回退）
     */
    private List<Affix> getAllAffixes() {
        if (allAffixes == null) {
            allAffixes = List.copyOf(AffixRegistry.getAll());
        }
        return allAffixes;
    }

    /**
     * 确保缓存有效，否则重建
     */
    private void ensureValid() {
        if (valid) return;

        ItemAffixBindingConfig config = AffixDataLoader.getItemAffixBindingConfig();
        if (config == null || config.getBindings() == null) {
            valid = true;
            return;
        }

        for (ItemAffixBindingConfig.Binding binding : config.getBindings()) {
            List<Affix> affixes = resolveAffixPool(binding.getAffixPool());

            if (binding.getItem() != null) {
                itemAffixCache.put(binding.getItem(), affixes);
            }

            if (binding.getTag() != null) {
                // 确保 tag 是标准格式，无 # 前缀
                String tagKey = binding.getTag().startsWith("#")
                        ? binding.getTag().substring(1)
                        : binding.getTag();
                tagAffixCache.merge(tagKey, affixes, (old, neu) -> {
                    Set<Affix> merged = new LinkedHashSet<>(old);
                    merged.addAll(neu);
                    return new ArrayList<>(merged);
                });
            }
        }

        valid = true;
    }

    /**
     * 根据词缀 ID 列表解析 Affix 实例
     */
    private List<Affix> resolveAffixPool(List<String> affixIds) {
        if (affixIds == null) return List.of();
        return affixIds.stream()
                .map(AffixRegistry::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
