package com.gmail.val59000mc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A class for reflective, hacky utils that are sometimes necessary.
 * @author Justsnoopy30
 */
public class HackyUtils {
    /**
     * A method to remove the final modifier from a field.
     * Note that you need to make the field accessible using field.setAccessible(true) first.
     * Thanks to StackOverflow for the original answer, and KennyTV (ViaVersion contributor)
     * for providing a way to practically use it.
     */
    public static void removeFinal(Field field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Remove the final modifier (unless already removed)
        if (Modifier.isFinal(field.getModifiers())) {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (NoSuchFieldException error) {
                // Java 12+ compatibility
                Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getDeclaredFields0.setAccessible(true);
                Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
                for (Field classField : fields) {
                    if ("modifiers".equals(classField.getName())) {
                        classField.setAccessible(true);
                        classField.set(field, field.getModifiers() & ~Modifier.FINAL);
                        break;
                    }
                }
            }
        }
    }
}
