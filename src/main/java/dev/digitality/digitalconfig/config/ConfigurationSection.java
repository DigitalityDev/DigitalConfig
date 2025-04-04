package dev.digitality.digitalconfig.config;

import dev.digitality.digitalconfig.DigitalConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ConfigurationSection {
    private Map<String, ConfigurationPath> data = new LinkedHashMap<>();
    private Map<String, List<?>> metadata = new HashMap<>();

    public void set(String path, ConfigurationPath value) {
        if (value == null) {
            value = new ConfigurationPath(null);
        }

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

    private <T> T get(String fullPath, String path, Class<T> type, Map<String, ConfigurationPath> currentMap) {
        if (currentMap.containsKey(path)) {
            return castData(fullPath, path, currentMap.get(path).getData(), type);
        }

        String[] parts = path.split("\\.");
        StringBuilder keyBuilder = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) keyBuilder.append(".");
            keyBuilder.append(parts[i]);

            String currentKey = keyBuilder.toString();

            if (currentMap.containsKey(currentKey)) {
                Object data = currentMap.get(currentKey).getData();

                if (path.length() == currentKey.length()) {
                    return type.cast(data);
                }

                if (data instanceof ConfigurationSection section) {
                    return get(
                            fullPath,
                            path.substring(currentKey.length() + 1),
                            type,
                            section.getData()
                    );
                }
            }
        }

        DigitalConfig.LOGGER.warn("Section {} was not found in path {}.", path, fullPath);
        return null;
    }

    private <T> T castData(String fullPath, String path, Object data, Class<T> type) {
        if (data == null) {
            return null;
        }

        if (type.isInstance(data)) {
            return type.cast(data);
        }

        throw new IllegalArgumentException("Value %s at path %s is of type %s, not %s.".formatted(path, fullPath, data.getClass().getName(), type.getName()));
    }

    public <T> T get(String path, Class<T> type) {
        return get(path, path, type, data);
    }

    public ConfigurationSection getSection(String path) {
        return get(path, ConfigurationSection.class);
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

        if (data != null && !data.isEmpty() && data.getFirst() instanceof String) {
            return (List<String>) data;
        }

        throw new IllegalArgumentException("Value at path %s is not of type List<String>.".formatted(path));
    }

    public List<String> getKeys() {
        return new ArrayList<>(data.keySet());
    }

    public boolean hasKey(String path) {
        return get(path, Object.class) != null;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
