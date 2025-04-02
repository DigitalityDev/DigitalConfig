package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.formats.IConfigFormat;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlConfigFormat implements IConfigFormat {
    private final Yaml yaml;

    public YamlConfigFormat() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);
        YamlConstructor constructor = new YamlConstructor(loaderOptions);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(4);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setNonPrintableStyle(DumperOptions.NonPrintableStyle.ESCAPE);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setProcessComments(true);
        YamlRepresenter representer = new YamlRepresenter(dumperOptions);

        this.yaml = new Yaml(constructor, representer, dumperOptions, loaderOptions);
    }

    @Override
    public ConfigurationSection deserialize(String content) {
        ConfigurationSection section = this.yaml.load(content);

        if (section == null)
            section = new ConfigurationSection();

        return section;
    }

    @Override
    public String serialize(ConfigurationSection section) {
        return this.yaml.dump(section);
    }
}
