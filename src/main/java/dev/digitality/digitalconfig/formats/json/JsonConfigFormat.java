package dev.digitality.digitalconfig.formats.json;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.formats.IConfigFormat;

import java.io.IOException;

public class JsonConfigFormat implements IConfigFormat {

    @Override
    public ConfigurationSection load(String content) {
        return null;
    }

    @Override
    public void save(Configuration config) throws IOException {

    }
}
