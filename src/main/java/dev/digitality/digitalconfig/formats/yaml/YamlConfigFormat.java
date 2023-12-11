package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.formats.IConfigFormat;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class YamlConfigFormat implements IConfigFormat {
    private final Yaml yaml;

    public YamlConfigFormat() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);
        YamlConstructor constructor = new YamlConstructor(loaderOptions);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(4);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setProcessComments(true);
        YamlRepresenter representer = new YamlRepresenter(dumperOptions);

        this.yaml = new Yaml(constructor, representer, dumperOptions, loaderOptions);
    }

    @Override
    public ConfigurationSection load(String content) {
        ConfigurationSection section = this.yaml.load(content);

        if (section == null)
            section = new ConfigurationSection();

        return section;
    }

    @Override
    public void save(Configuration config) throws IOException {
        String data = this.yaml.dump(config);
        Files.writeString(config.getPath(), data, StandardCharsets.UTF_8);
    }
}
