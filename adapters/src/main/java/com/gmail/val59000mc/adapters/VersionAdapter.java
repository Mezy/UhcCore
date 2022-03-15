package com.gmail.val59000mc.adapters;

import com.gmail.val59000mc.adapters.impl.DefaultVersionAdapterImpl;

import org.bukkit.Bukkit;

/**
 * Utility class exposing an abstraction layer for functionality where a
 * custom implementation needs to be used depending on the runtime server version.
 */
public abstract class VersionAdapter {

    /**
     * Loads and instantiates a {@link VersionAdapter} for the running server version.
     *
     * @return the adapter instance
     * @throws InstantiationException if the adapter couldn't be instantiated
     */
    public static VersionAdapter instantiate() throws InstantiationException {
        try {
            final Class<? extends VersionAdapter> loadedClass = loadAdapterImplClass();
            return loadedClass.getConstructor().newInstance();
        } catch (ClassNotFoundException ignored) {
            return new DefaultVersionAdapterImpl();
        } catch (Exception e) {
            throw new InstantiationException(e);
        }
    }

    public static class InstantiationException extends Exception {
        public InstantiationException(Throwable cause) {
            super("Unable to instantiate version adapter", cause);
        }
    }

    /**
     * Gets the NMS package name of the running server, such as {@code v1_18_R2}.
     *
     * @return the NMS package name
     */
    private static String getNmsPackageName() {
        final String nmsPackageName = Bukkit.getServer().getClass().getPackage().getName();
        return nmsPackageName.substring(nmsPackageName.lastIndexOf('.') + 1);
    }

    /**
     * Loads and returns the adapter implementation class for the running server version.
     *
     * @return the adapter implementation class
     * @throws ClassNotFoundException if the class cannot be loaded
     */
    @SuppressWarnings("unchecked")
    private static Class<? extends VersionAdapter> loadAdapterImplClass() throws ClassNotFoundException {
        final String implPackageName = DefaultVersionAdapterImpl.class.getPackage().getName();
        final String nmsImplPackageName = implPackageName + '.' + getNmsPackageName();
        final String implClassName = VersionAdapter.class.getSimpleName() + "Impl";
        return (Class<? extends VersionAdapter>) Class.forName(nmsImplPackageName + '.' + implClassName);
    }

    /**
     * Removes oceans from the world generation.
     * <p>
     * This method should be called before the world is generated.
     *
     * @throws UnsupportedOperationException if not supported by this adapter
     */
    public abstract void removeOceans();

}
