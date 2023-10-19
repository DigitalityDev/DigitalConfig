package dev.digitality.digitalconfig.formats;

import java.util.HashMap;
import java.util.Objects;

public interface ConfigFormat {
    HashMap<String, Object> load(String content);
    void save(HashMap<String, Object> config);
}