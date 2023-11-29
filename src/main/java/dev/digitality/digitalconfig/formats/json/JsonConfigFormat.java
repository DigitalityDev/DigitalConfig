package dev.digitality.digitalconfig.formats.json;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.formats.ConfigFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonConfigFormat implements ConfigFormat {

    @Override
    public ConfigurationSection load(String content) {
        return null;
    }

    @Override
    public void save(Configuration config) throws IOException {

    }
}
