package dev.digitality.digitalconfig.config;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConfigurationValue {
    private Object data;
    private final Map<String, List<?>> metadata = new HashMap<>();

    public ConfigurationValue(Object data) {
        this.data = data;
    }
}