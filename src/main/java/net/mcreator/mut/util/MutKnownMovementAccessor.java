package net.mcreator.mut.util;

import net.minecraft.world.phys.Vec3;

public interface MutKnownMovementAccessor {
    Vec3 mutGetKnownMovement();
    void mutSetKnownMovement(Vec3 vec3);
}