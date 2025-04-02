package dev.digitality.digitalconfig.config;

import dev.digitality.digitalconfig.DigitalConfig;
import dev.digitality.digitalconfig.formats.ConfigFormat;
import dev.digitality.digitalconfig.formats.IConfigFormat;
import lombok.Cleanup;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class Configuration extends ConfigurationSection {
    private final Path path;
    private final IConfigFormat format;

    /**
     * This is used to instantiate a configuration file.
     * The file format will be inferred from the file extension.
     *
     * @param filePath The path to the config file.
     * @throws IllegalArgumentException If the file extension is not supported.
     */
    public Configuration(String filePath) {
        this(filePath, true);
    }

    /**
     * This is used to instantiate a configuration file.
     * The file format will be inferred from the file extension.
     *
     * @param filePath The path to the config file.
     * @param createEmpty Whether to create a new file if the specified file does not exist.
     * @throws IllegalArgumentException If the file extension is not supported.
     */
    public Configuration(String filePath, boolean createEmpty) {
        this(filePath, ConfigFormat.getByExtension(filePath.substring(filePath.lastIndexOf('.') + 1)), createEmpty);
    }

    /**
     * This is used to instantiate a configuration file.
     *
     * @param filePath The path to the config file.
     * @param format The format of the config file.
     */
    public Configuration(String filePath, IConfigFormat format) {
        this(filePath, format, true);
    }

    /**
     * This is used to instantiate a configuration file.
     *
     * @param filePath The path to the config file.
     * @param format The format of the config file.
     * @param createEmpty Whether to create a new file if the specified file does not exist.
     */
    public Configuration(String filePath, IConfigFormat format, boolean createEmpty) {
        this.path = Path.of(filePath);
        this.format = format;

        if (!path.toFile().exists() && createEmpty) {
            try {
                Files.createDirectories(path.toAbsolutePath().getParent());
                Files.createFile(path);
            } catch (IOException e) {
                DigitalConfig.LOGGER.error("Failed to create an empty config at {}!", path, e);
                return;
            }
        }

        if (path.toFile().exists())
            load();
    }

    /**
     * This is used to create a default configuration file with the same path in the resources folder as the config file.
     */
    public void createDefault() {
        createDefault(path.getFileName().toString());
    }

    /**
     * This is used to create a default configuration file.
     * If there is a file at the specified path in the "resources" folder, it will be copied. Otherwise, an exception will be thrown.
     *
     * @param resourcePath The path to the resource file in the "resources" folder.
     * If the path is the same as the path of the config file, you can use {@link #createDefault()} instead.
     * @throws IllegalArgumentException If the specified resource file does not exist.
     */
    public void createDefault(String resourcePath) {
        if (path.toFile().exists() && path.toFile().length() > 0)
            return;

        try {
            @Cleanup
            InputStream resource = DigitalConfig.class.getResourceAsStream(resourcePath);
            if (resource == null)
                throw new IllegalArgumentException("Resource file at " + resourcePath + " does not exist!");

            Files.write(path, resource.readAllBytes());
        } catch (IOException e) {
            DigitalConfig.LOGGER.error("Failed to create a default config at {}!", path, e);
        }

        load();
    }

    /**
     * This is used to load the configuration file.
     */
    public void load() {
        try {
            ConfigurationSection config = format.deserialize(Files.readString(path, StandardCharsets.UTF_8));

            this.setData(config.getData());
            this.setMetadata(config.getMetadata());
        } catch (IOException e) {
            DigitalConfig.LOGGER.error("Failed to load config at {}!", path, e);
        }
    }

    /**
     * This is used to save the configuration file.
     */
    public void save() {
        try {
            Files.writeString(path, format.serialize(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            DigitalConfig.LOGGER.error("Failed to save config at {}!", path, e);
        }
    }
}