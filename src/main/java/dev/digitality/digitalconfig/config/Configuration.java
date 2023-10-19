package dev.digitality.digitalconfig.config;

import com.google.common.io.Resources;
import dev.digitality.digitalconfig.DigitalConfig;
import dev.digitality.digitalconfig.formats.ConfigFormat;
import dev.digitality.digitalconfig.formats.JsonConfigFormat;
import dev.digitality.digitalconfig.formats.YamlConfigFormat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration extends ConfigurationSection {
    private final Path path;
    private final ConfigFormat format;

    /**
     * This is used to instantiate a configuration file.
     * The file format will be inferred from the file extension.
     *
     * @param filePath The path to the config file.
     * @param createDefault Whether to create AND load a default config file if the specified file does not exist.
     * @throws IllegalArgumentException If the file extension is not supported.
     */
    public Configuration(String filePath, boolean createDefault) {
        this(filePath, getFormatFromExtension(filePath.substring(filePath.lastIndexOf('.') + 1)), createDefault);
    }

    /**
     * This is used to instantiate a configuration file.
     *
     * @param filePath The path to the config file.
     * @param format The format of the config file.
     * @param createDefault Whether to create AND load a default config file if the specified file does not exist.
     */
    public Configuration(String filePath, ConfigFormat format, boolean createDefault) {
        this.path = Path.of(filePath);
        this.format = format;

        if (createDefault) {
            createDefault();
            load();
        }
    }

    private static ConfigFormat getFormatFromExtension(String extension) {
        return switch (extension) {
            case "json" -> new JsonConfigFormat();
            case "yml", "yaml" -> new YamlConfigFormat();

            default -> throw new IllegalArgumentException("Unsupported config format!");
        };
    }

    /**
     * This is used to create a default configuration file.
     * If there is a file at the same path in the "resources" folder, it will be copied. Otherwise, a new empty file will be created.
     */
    public void createDefault() {
        createDefault(path.getFileName().toString());
    }

    /**
     * This is used to create a default configuration file.
     * If there is a file at the specified path in the "resources" folder, it will be copied. Otherwise, a new empty file will be created and an exception will be thrown.
     *
     * @param resourcePath The path to the resource file in the "resources" folder.
     * If the path is the same as the path of the config file, you can use {@link #createDefault()} instead.
     * @throws IllegalArgumentException If the specified resource file does not exist.
     */
    public void createDefault(String resourcePath) {
        if (path.toFile().exists()) return;

        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            DigitalConfig.LOGGER.error("Failed to create a default config at " + path + "!", e);
            return;
        }

        try {
            URL resource = Resources.getResource(resourcePath);
            Resources.copy(resource, Files.newOutputStream(path));
        } catch (IOException e) {
            DigitalConfig.LOGGER.error("Failed to create a default config at " + path + "!", e);
        } catch (IllegalArgumentException e) {
            if (!path.equals(Path.of(resourcePath)))
                throw e;
        }
    }
}