package dev.digitality.digitalconfig.serialization;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationSerialization {
    public static final String SERIALIZED_KEY = "==";
    private static final Map<String, Class<?>> aliases = new HashMap<>();

    static {
        registerAliases();
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
            Class<?> clazz = getClassByName(alias);

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
        } catch (ClassNotFoundException ignored) {}

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

    private static void registerAliases() {
        Reflections reflections = new Reflections();

        reflections.getTypesAnnotatedWith(SerializableAs.class).forEach(c -> {
            aliases.put(c.getAnnotation(SerializableAs.class).value(), c);
        });

        try {
            Class<? extends Annotation> bukkitClass = (Class<? extends Annotation>) Class.forName("org.bukkit.configuration.serialization.SerializableAs");
            Method valueMethod = bukkitClass.getMethod("value");

            for (Class<?> c : reflections.getTypesAnnotatedWith(bukkitClass))
                aliases.put((String) valueMethod.invoke(c.getAnnotation(bukkitClass)), c);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {}
    }

    private static Class<?> getClassByName(String name) throws ClassNotFoundException {
        if (aliases.containsKey(name))
            return aliases.get(name);

        return Class.forName(name);
    }

    private static String getAlias(Class<?> clazz) {
        return aliases
                .entrySet()
                .stream()
                .filter(e -> clazz == e.getValue())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(clazz::getName);
    }
}
