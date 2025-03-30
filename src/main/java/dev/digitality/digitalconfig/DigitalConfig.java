package dev.digitality.digitalconfig;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.formats.ConfigFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(DigitalConfig.class);

    public static void main(String[] args) {
        Configuration config = new Configuration("eula.txt", ConfigFormat.PROPERTIES.getFormat());
        config.get("eula", String.class);
        config.set("eula", "true");

        config.save();
    }
}