package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.formats.ConfigFormat;
import lombok.Cleanup;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class YamlConfigFormat implements ConfigFormat {
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
