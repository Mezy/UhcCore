package com.gmail.val59000mc.adapters.impl.v1_18_R2;

import java.util.OptionalInt;

import com.gmail.val59000mc.adapters.VersionAdapter;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.DensityFunctions.MarkerOrMarked;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class VersionAdapterImpl extends VersionAdapter {

    /**
     * See {@link NoiseRouterData#CONTINENTS}.
     */
    private final ResourceKey<DensityFunction> continentsKey = ResourceKey.create(Registry.DENSITY_FUNCTION_REGISTRY, new ResourceLocation("overworld/continents"));

    @Override
    public void removeOceans() {
        RegistryUtils.obtainWriteAccess(Registry.DENSITY_FUNCTION_REGISTRY, dfRegistry -> {
            // The continents noise function is wrapped in a flatCache, see NoiseRouterData#bootstrap().
            final MarkerOrMarked continents = (MarkerOrMarked) dfRegistry.getOrThrow(continentsKey);
            // We keep the flatCache for performance, but take the absolute value of the noise function.
            // This stops ocean generation, but keeps the qualities of the original noise.
            final DensityFunction continentsWithoutOceans = DensityFunctions.flatCache(continents.wrapped().abs());
            dfRegistry.registerOrOverride(OptionalInt.empty(), continentsKey, continentsWithoutOceans, Lifecycle.stable());
        });
    }

}
