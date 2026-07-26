package net.mcreator.mut.trait;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class Trait {
    private final String id;
    private final String nameKey;
    private final List<String> descriptionKeys;
    private final List<TraitEffect> effects;
    // 匹配规则
    private final List<String> matchItems;     // "minecraft:diamond_sword"
    private final List<TagKey<Item>> matchTags; // "#minecraft:swords"

    public Trait(String id, String nameKey, List<String> descriptionKeys,
                 List<TraitEffect> effects,
                 List<String> matchItems, List<TagKey<Item>> matchTags) {
        this.id = id;
        this.nameKey = nameKey;
        this.descriptionKeys = descriptionKeys;
        this.effects = effects;
        this.matchItems = matchItems;
        this.matchTags = matchTags;
    }

    public String getId() { return id; }
    public String getNameKey() { return nameKey; }
    public List<String> getDescriptionKeys() { return descriptionKeys; }
    public List<TraitEffect> getEffects() { return effects; }

    /** 判断物品是否匹配此特性 */
    public boolean matches(ItemStack stack) {
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        // 精确 ID 匹配
        if (matchItems.stream().anyMatch(s -> s.equals(stackId.toString()))) return true;
        // 标签匹配
        return matchTags.stream().anyMatch(stack::is);
    }
}