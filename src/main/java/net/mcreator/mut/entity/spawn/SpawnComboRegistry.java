package net.mcreator.mut.entity.spawn;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SpawnComboRegistry {

    private static final Map<String, SpawnCombo> COMBOS = new LinkedHashMap<>();

    private SpawnComboRegistry() {}

    public static void register(SpawnCombo combo) {
        COMBOS.put(combo.id(), combo);
    }

    public static void clear() {
        COMBOS.clear();
    }

    public static List<SpawnCombo> getAll() {
        return new ArrayList<>(COMBOS.values());
    }

    public static List<SpawnCombo> forHost(EntityType<?> hostType) {
        List<SpawnCombo> result = new ArrayList<>();
        for (SpawnCombo combo : COMBOS.values()) {
            if (combo.hostType() == hostType) result.add(combo);
        }
        return result;
    }
}
