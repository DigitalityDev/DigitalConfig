package dev.digitality.digitalconfig.serialization;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ConfigurationSerialization {
    public static final String SERIALIZED_KEY = "==";
    private static final Reflections reflections = new Reflections();
    private static final Map<String, Class<?>> aliases = new HashMap<>();

    static {
        registerClasses();
    }

    public static boolean isSerializable(Object obj) {
        if (obj instanceof ConfigurationSerializable)
            return true;

        try {
            Class<?> clazz = Class.forName("org.bukkit.configuration.serialization.ConfigurationSerializable");
            return clazz.isAssignableFrom(obj.getClass());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Map<String, Object> serialize(Object obj) {
        if (!isSerializable(obj))
            throw new IllegalArgumentException(obj.getClass() + " does not implement ConfigurationSerializable!");

        Map<String, Object> result = new HashMap<>();
        result.put(SERIALIZED_KEY, getAlias(obj.getClass()));

        try {
            result.putAll((Map<String, Object>) obj.getClass().getMethod("serialize").invoke(obj));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Optional<Object> deserialize(Map<String, Object> args) {
        if (!args.containsKey(SERIALIZED_KEY))
            return Optional.empty();

        try {
            String alias = String.valueOf(args.get(SERIALIZED_KEY));
            Class<?> clazz = aliases.get(alias);

            if (clazz == null) {
                throw new IllegalArgumentException(alias + " is not a valid ConfigurationSerializable!");
            }

            Method deserializeMethod = getMethod(clazz, "deserialize");
            if (deserializeMethod != null)
                return Optional.of(deserializeMethod.invoke(null, args));

            Method valueOfMethod = getMethod(clazz, "valueOf");
            if (valueOfMethod != null)
                return Optional.of(valueOfMethod.invoke(null, args));

            Constructor<?> constructor = getConstructor(clazz);
            if (constructor != null)
                return Optional.of(constructor.newInstance(args));
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private static Method getMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName, Map.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            return null;
        }
    }

    private static Constructor<?> getConstructor(Class<?> clazz) {
        try {
            return clazz.getConstructor(Map.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            return null;
        }
    }

    private static void registerClasses() {
        Set<Class<?>> classes = new HashSet<>(reflections.getSubTypesOf(ConfigurationSerializable.class));
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.configuration.serialization.ConfigurationSerializable");
            classes.addAll(reflections.getSubTypesOf(bukkitClass));
        } catch (ClassNotFoundException ignored) {}

        classes.forEach((clazz) -> {
            Object delegate = getAnnotationValue(clazz, DelegateDeserialization.class);

            if (delegate == null) {
                aliases.put(getAlias(clazz), clazz);
                aliases.put(clazz.getName(), clazz);
            }
        });
    }

    private static String getAlias(Class<?> clazz) {
        Object delegate = getAnnotationValue(clazz, DelegateDeserialization.class);

        if (delegate != null) {
            return getAlias((Class<?>) delegate);
        }

        Object alias = getAnnotationValue(clazz, SerializableAs.class);

        if (alias != null) {
            return (String) alias;
        }

        return clazz.getName();
    }

    private static Object getAnnotationValue(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Annotation annotation = clazz.getAnnotation(annotationClass);

        try {
            if (annotation == null) {
                Class<? extends Annotation> bukkitClass = (Class<? extends Annotation>) Class.forName("org.bukkit.configuration.serialization." + annotationClass.getSimpleName());

                if (clazz.getAnnotation(bukkitClass) == null) {
                    return null;
                }

                annotation = clazz.getAnnotation(bukkitClass);
            }

            Method valueMethod = annotation.getClass().getMethod("value");
            return valueMethod.invoke(annotation);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException ignored) {}

        return null;
    }
}
