package net.mcreator.mut.trait;

import net.minecraft.world.item.ItemStack;
import java.util.*;

public class TraitRegistry {
    private static final Map<String, Trait> TRAITS = new LinkedHashMap<>();

    public static void register(Trait trait) { TRAITS.put(trait.getId(), trait); }
    public static void clear() { TRAITS.clear(); }

    public static Trait get(String id) { return TRAITS.get(id); }
    public static Collection<Trait> getAll() { return TRAITS.values(); }

    public static List<Trait> getTraitsFor(ItemStack stack) {
        List<Trait> result = new ArrayList<>();
        for (Trait trait : TRAITS.values()) {
            if (trait.matches(stack)) result.add(trait);
        }
        return result;
    }
}