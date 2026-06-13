package net.mcreator.mut.block.entity.trial_spawner;

import net.minecraft.util.StringRepresentable;

public enum TrialSpawnerState implements StringRepresentable {
    INACTIVE("inactive"),
    ACTIVE("active"),
    EJECTING("ejecting"),
    COOLDOWN("cooldown");

    private final String name;

    TrialSpawnerState(String name) { this.name = name; }

    @Override
    public String getSerializedName() { return name; }
}