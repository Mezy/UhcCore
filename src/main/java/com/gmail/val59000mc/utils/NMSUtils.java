package com.gmail.val59000mc.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSUtils {

    private static String version = getVersion();

    public static String getVersion() {
        if (version != null) {
            return version;
        } else {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            return name.substring(name.lastIndexOf(46) + 1) + ".";
        }
    }

    public static Object getHandle(Object o) {
        try {
            return getMethod(o.getClass(), "getHandle").invoke(o);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
            return null;
        }
    }

    public static Object getServer(Object o) {
        try {
            return getMethod(o.getClass(), "getServer").invoke(o);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
            return null;
        }
    }

    public static Method getMethod(Class<?> c, String name) {
        return getMethod(c, name, -1);
    }

    public static Method getMethod(Class<?> c, String name, int args) {

        for (Method method : c.getMethods()){
            if (method.getName().equals(name) && (args == -1 || method.getParameterCount() == args)){
                method.setAccessible(true);
                return method;
            }
        }

        for (Method method : c.getDeclaredMethods()){
            if (method.getName().equals(name)){
                method.setAccessible(true);
                return method;
            }
        }

        return null;
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return getClassWithException(name);
        } catch (ClassNotFoundException var4) {
            try {
                return getCraftClassWithException(name);
            } catch (ClassNotFoundException var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    private static Class<?> getClassWithException(String name) throws ClassNotFoundException {
        String classname = "net.minecraft.server." + getVersion() + name;
        return Class.forName(classname);
    }

    private static Class<?> getCraftClassWithException(String name) throws ClassNotFoundException {
        String classname = "org.bukkit.craftbukkit." + getVersion() + name;
        return Class.forName(classname);
    }

}