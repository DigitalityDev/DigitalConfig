package dev.digitality.digitalconfig.formats;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;

import java.io.IOException;
import java.util.Map;

public interface ConfigFormat {
    ConfigurationSection load(String content);

    void save(Configuration config) throws IOException;
}