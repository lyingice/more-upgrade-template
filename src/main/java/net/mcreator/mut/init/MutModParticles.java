package net.mcreator.mut.init;

import net.mcreator.mut.MutMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MutModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, MutMod.MODID);

    public static final Supplier<SimpleParticleType> GRAY_SONIC_BOOM =
            PARTICLES.register("gray_sonic_boom",
                    () -> new SimpleParticleType(false));
}