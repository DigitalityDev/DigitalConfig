package dev.digitality.digitalconfig;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;

import java.util.List;
import java.util.Map;

public class DigitalConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(DigitalConfig.class);

    public static void main(String[] args) {
        Configuration config = new Configuration("config.yml");
        config.save();
    }
}