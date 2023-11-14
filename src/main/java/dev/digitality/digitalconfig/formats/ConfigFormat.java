package dev.digitality.digitalconfig.formats;

import java.util.HashMap;

public interface ConfigFormat {
    HashMap<String, Object> load(String content);

    void save(HashMap<String, Object> config);
}