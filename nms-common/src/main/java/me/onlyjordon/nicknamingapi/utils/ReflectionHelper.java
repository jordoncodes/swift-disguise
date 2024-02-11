package me.onlyjordon.nicknamingapi.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {

    public static <T> Field getField(String fieldName, T object) throws NoSuchFieldException {
        return object.getClass().getDeclaredField(fieldName);
    }

    public static Field getField(String fieldName, Class<?> clazz) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @NotNull
    public static <T> Method getMethod(@NotNull String methodName, @NotNull Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;

    }

    public static <T,T2> void setFieldValue(@NotNull String field, @NotNull T value, @NotNull T2 instance) throws NoSuchFieldException, IllegalAccessException {
        Field f = getField(field, instance);
        f.setAccessible(true);
        f.set(instance, value);
    }
}
