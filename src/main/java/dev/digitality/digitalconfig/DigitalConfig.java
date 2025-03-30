package dev.digitality.digitalconfig;

import dev.digitality.digitalconfig.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(DigitalConfig.class);

    public static void main(String[] args) {
        Configuration config = new Configuration("velocity.toml");
        System.out.println(config.get("config-version", String.class));
        config.set("forced-hosts", null);
        config.set("servers.lobby", "nigger");

        config.save();
    }
}