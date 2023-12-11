package dev.digitality.digitalconfig.formats;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;

import java.io.IOException;

public interface IConfigFormat {
    ConfigurationSection load(String content);

    void save(Configuration config) throws IOException;
}