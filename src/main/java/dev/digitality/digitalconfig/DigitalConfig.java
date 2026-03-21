package dev.digitality.digitalconfig;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(DigitalConfig.class);

    @Getter @Setter
    private static boolean debug = false;
}
