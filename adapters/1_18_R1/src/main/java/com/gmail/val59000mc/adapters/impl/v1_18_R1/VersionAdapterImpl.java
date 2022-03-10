package com.gmail.val59000mc.adapters.impl.v1_18_R1;

import java.util.OptionalInt;

import com.gmail.val59000mc.adapters.VersionAdapter;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class VersionAdapterImpl extends VersionAdapter {

    @Override
    public void removeOceans() {
        // Setting the continentalness noise to a constant 0 will stop ocean generation.
        // It may also reduce the quality of the terrain generation, but without
        // noise routers (introduced in 1.18.2), this seems like the best solution.
        final NoiseParameters continentalnessWithoutOceans = new NoiseParameters(0, 0);
        RegistryUtils.obtainWriteAccess(Registry.NOISE_REGISTRY, noiseRegistry -> {
            noiseRegistry.registerOrOverride(OptionalInt.empty(), Noises.CONTINENTALNESS, continentalnessWithoutOceans, Lifecycle.stable());
        });
    }

}
