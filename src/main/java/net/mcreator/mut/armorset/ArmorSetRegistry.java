package net.mcreator.mut.armorset;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ArmorSetRegistry {

    private static final Map<String, ArmorSet> SETS = new LinkedHashMap<>();

    private ArmorSetRegistry() {}

    public static void register(ArmorSet set) {
        SETS.put(set.getId(), set);
    }

    public static void clear() {
        SETS.clear();
    }

    public static ArmorSet get(String id) {
        return SETS.get(id);
    }

    public static List<ArmorSet> getAll() {
        return new ArrayList<>(SETS.values());
    }

    /** 某个物品所属的全部套装（用于提示） */
    public static List<ArmorSet> getSetsFor(ItemStack stack) {
        List<ArmorSet> result = new ArrayList<>();
        for (ArmorSet set : SETS.values()) {
            if (set.matches(stack)) result.add(set);
        }
        return result;
    }
}
