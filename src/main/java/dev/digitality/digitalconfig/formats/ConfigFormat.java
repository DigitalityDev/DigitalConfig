package dev.digitality.digitalconfig.formats;

import dev.digitality.digitalconfig.formats.json.JsonConfigFormat;
import dev.digitality.digitalconfig.formats.yaml.YamlConfigFormat;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum ConfigFormat {
    YAML(new YamlConfigFormat(), "yaml", "yml"),
    JSON(new JsonConfigFormat(), "json");

    private final IConfigFormat format;
    private final List<String> extensions;

    ConfigFormat(IConfigFormat format, String... extensions) {
        this.format = format;
        this.extensions = Arrays.asList(extensions);
    }

    public static IConfigFormat getByExtension(String extension) {
        Optional<ConfigFormat> format = Arrays.stream(ConfigFormat.values())
                .filter(f -> f.getExtensions().contains(extension.toLowerCase()))
                .findFirst();

        if (format.isEmpty())
            throw new IllegalArgumentException("Unsupported file extension: " + extension);

        return format.get().getFormat();
    }
}
