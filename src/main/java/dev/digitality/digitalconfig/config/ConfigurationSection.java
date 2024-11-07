package dev.digitality.digitalconfig.config;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.comments.CommentLine;

import java.util.*;

@Getter
@Setter
public class ConfigurationSection {
    private Map<String, ConfigurationPath> data = new LinkedHashMap<>();
    private List<CommentLine> headerComments = new ArrayList<>();
    private List<CommentLine> footerComments = new ArrayList<>();

    public void set(String path, ConfigurationPath value) {
        data.put(path, value);
    }

    public void set(String path, Object value) {
        data.put(path, new ConfigurationPath(value));
    }

    public void get(String path) {
        data.get(path);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
