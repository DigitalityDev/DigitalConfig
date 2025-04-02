package dev.digitality.digitalconfig.config;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConfigurationPath {
    private Object data;
    private final Map<String, List<?>> metadata = new HashMap<>();

    public ConfigurationPath(Object data) {
        this.data = data;
    }
}