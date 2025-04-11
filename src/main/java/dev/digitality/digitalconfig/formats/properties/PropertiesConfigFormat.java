package dev.digitality.digitalconfig.formats.properties;

import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.config.ConfigurationValue;
import dev.digitality.digitalconfig.formats.IConfigFormat;
import org.codejive.properties.Properties;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class PropertiesConfigFormat implements IConfigFormat {
    @Override
    public ConfigurationSection deserialize(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        ConfigurationSection section = new ConfigurationSection();

        for (String key : properties.stringPropertyNames()) {
            ConfigurationValue path = new ConfigurationValue(properties.getProperty(key));
            path.getMetadata().put("properties.comments", properties.getComment(key));

            section.getData().put(key, path);
        }

        return section;
    }

    @Override
    public String serialize(ConfigurationSection section) throws IOException {
        Properties properties = new Properties();

        for (Map.Entry<String, ConfigurationValue> entry : section.getData().entrySet()) {
            String key = entry.getKey();
            ConfigurationValue value = entry.getValue();

            properties.setProperty(key, String.valueOf(value.getData()));
            if (value.getMetadata().containsKey("properties.comments")) {
                properties.setComment(key, (List<String>) value.getMetadata().get("properties.comments"));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        properties.store(writer);
        writer.flush();
        outputStream.flush();

        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
