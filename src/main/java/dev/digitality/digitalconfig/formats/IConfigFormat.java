package dev.digitality.digitalconfig.formats;

import dev.digitality.digitalconfig.config.ConfigurationSection;

import java.io.IOException;

public interface IConfigFormat {
    ConfigurationSection deserialize(String content) throws IOException;

    String serialize(ConfigurationSection section) throws IOException;
}