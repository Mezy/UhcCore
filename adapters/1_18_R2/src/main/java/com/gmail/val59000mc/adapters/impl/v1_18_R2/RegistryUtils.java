package com.gmail.val59000mc.adapters.impl.v1_18_R2;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;

/**
 * Utility class for dealing with the NMS registry.
 */
public abstract class RegistryUtils {

	private static final RegistryAccess registryAccess = getRegistryAccess();

	private static RegistryAccess getRegistryAccess() {
		final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
		return server.registryAccess();
	}

	/**
	 * Obtains write access to a given registry and runs an operation on it.
	 *
	 * @param <T> the type of registry
	 * @param registryKey the key for the registry
	 * @param writeOperation the operation to run with write access
	 */
    public static <T> void obtainWriteAccess(ResourceKey<Registry<T>> registryKey, Consumer<WritableRegistry<T>> writeOperation) {
		final MappedRegistry<T> registry = (MappedRegistry<T>) registryAccess.ownedRegistryOrThrow(registryKey);
		try {
			// Starting from 1.18.2, registries are frozen after initialization,
			// which happens before any plugins are loaded. This is a workaround
			// to allow writing to the registry by temporarily unfreezing it again.
			final Field frozen = MappedRegistry.class.getDeclaredField("bL");
			frozen.setAccessible(true);
			frozen.set(registry, false);
            writeOperation.accept(registry);
            frozen.set(registry, true);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Unable to get registry write access", e);
		}
	}

}
