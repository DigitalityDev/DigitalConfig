package dev.digitality.digitalconfig.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ConfigurationSection {
    private Map<String, ConfigurationPath> data = new LinkedHashMap<>();
    private List<?> headerComments = new ArrayList<>();
    private List<?> footerComments = new ArrayList<>();

    public void set(String path, ConfigurationPath value) {
        String[] parts = path.split("\\.");
        Map<String, ConfigurationPath> currentMap = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object data = currentMap.computeIfAbsent(parts[i], k -> new ConfigurationPath(new ConfigurationSection())).getData();

            if (!(data instanceof ConfigurationSection section)) {
                throw new IllegalArgumentException("Section %s of path %s is not a valid configuration section.".formatted(parts[i], path));
            }

            currentMap = section.getData();
        }

        if (currentMap.containsKey(parts[parts.length - 1])) {
            ConfigurationPath existingValue = currentMap.get(parts[parts.length - 1]);
            existingValue.setData(value.getData());

            return;
        }

        currentMap.put(parts[parts.length - 1], value);
    }

    public void set(String path, Object value) {
        set(path, new ConfigurationPath(value));
    }

    public <T> T get(String path, Class<T> type) {
        String[] parts = path.split("\\.");
        Map<String, ConfigurationPath> currentMap = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object data = currentMap.get(parts[i]).getData();

            if (!(data instanceof ConfigurationSection section)) {
                throw new IllegalArgumentException("Section %s of path %s is not a valid configuration section.".formatted(parts[i], path));
            }

            currentMap = section.getData();
        }

        Object data = currentMap.get(parts[parts.length - 1]).getData();

        if (data == null) {
            return null;
        }

        if (!type.isInstance(data)) {
            throw new IllegalArgumentException("Value at path %s is not of type %s.".formatted(path, type.getName()));
        }

        return type.cast(data);
    }

    public String getString(String path) {
        return get(path, String.class);
    }

    public boolean getBoolean(String path) {
        return get(path, Boolean.class);
    }

    public int getInt(String path) {
        return get(path, Integer.class);
    }

    public long getLong(String path) {
        return get(path, Long.class);
    }

    public float getFloat(String path) {
        return get(path, Float.class);
    }

    public double getDouble(String path) {
        return get(path, Double.class);
    }

    public List<String> getStringList(String path) {
        List<?> data = get(path, List.class);

        if (data != null && !data.isEmpty() && data.get(0) instanceof String) {
            return (List<String>) data;
        }

        throw new IllegalArgumentException("Value at path %s is not of type List<String>.".formatted(path));
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
