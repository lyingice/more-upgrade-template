package net.mcreator.mut.armorset;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 一套护甲的定义：哪些物品算这一套 + 分件数的效果。
 * 与特性（Trait）分开维护，避免和主手特性混在一起。
 */
public class ArmorSet {

    public static final List<EquipmentSlot> SLOTS = List.of(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    /** 达到 pieces 件时启用的一组效果 */
    public record Bonus(int pieces, String descKey, List<ArmorSetEffect> effects) {}

    private final String id;
    private final String nameKey;
    private final List<String> pieceItems;
    private final List<TagKey<Item>> pieceTags;
    private final List<Bonus> bonuses;

    public ArmorSet(String id, String nameKey, List<String> pieceItems,
                    List<TagKey<Item>> pieceTags, List<Bonus> bonuses) {
        this.id = id;
        this.nameKey = nameKey;
        this.pieceItems = pieceItems;
        this.pieceTags = pieceTags;
        this.bonuses = bonuses;
    }

    public String getId() { return id; }
    public String getNameKey() { return nameKey; }
    public List<Bonus> getBonuses() { return bonuses; }
    public List<String> getPieceItems() { return pieceItems; }

    /** 判断某个物品是否属于这一套 */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (pieceItems.stream().anyMatch(s -> s.equals(itemId.toString()))) return true;
        return pieceTags.stream().anyMatch(stack::is);
    }

    /** 统计玩家当前穿着几件（头/胸/腿/靴） */
    public int countWorn(Player player) {
        int worn = 0;
        for (EquipmentSlot slot : SLOTS) {
            if (matches(player.getItemBySlot(slot))) worn++;
        }
        return worn;
    }
}
