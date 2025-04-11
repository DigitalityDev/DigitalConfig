package dev.digitality.digitalconfig.formats.toml;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.config.ConfigurationValue;
import dev.digitality.digitalconfig.formats.IConfigFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TOMLConfigFormat implements IConfigFormat {
    @Override
    public ConfigurationSection deserialize(String content) {
        Toml toml = new Toml().read(content);

        return fromMap(toml.toMap());
    }

    @Override
    public String serialize(ConfigurationSection section) {
        TomlWriter tomlWriter = new TomlWriter.Builder()
                .indentValuesBy(0)
                .indentTablesBy(0)
                .padArrayDelimitersBy(0)
                .build();

        return tomlWriter.write(toMap(section));
    }

    private Map<String, Object> toMap(ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String, ConfigurationValue> entry : section.getData().entrySet()) {
            Object data = entry.getValue().getData();

            if (data instanceof ConfigurationSection) {
                map.put(entry.getKey(), toMap((ConfigurationSection) data));
            } else {
                map.put(entry.getKey(), data);
            }
        }

        return map;
    }

    private ConfigurationSection fromMap(Map<String, Object> map) {
        ConfigurationSection section = new ConfigurationSection();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                section.getData().put(entry.getKey(), new ConfigurationValue(fromMap((Map<String, Object>) entry.getValue())));
            } else if (entry.getValue() instanceof Iterable) {
                List<Object> listSection = new ArrayList<>();

                for (Object item : (Iterable<?>) entry.getValue()) {
                    if (item instanceof Map) {
                        listSection.add(fromMap((Map<String, Object>) item));
                    } else {
                        listSection.add(item);
                    }
                }

                section.getData().put(entry.getKey(), new ConfigurationValue(listSection));
            } else {
                section.getData().put(entry.getKey(), new ConfigurationValue(entry.getValue()));
            }
        }

        return section;
    }
}
