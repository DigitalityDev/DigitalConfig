package dev.digitality.digitalconfig.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigurationPath {
    private Object data;
    private List<?> comments;
    private List<?> inlineComments;

    public ConfigurationPath(Object data) {
        this.data = data;
        this.comments = new ArrayList<>();
        this.inlineComments = new ArrayList<>();
    }

    public ConfigurationPath(Object data, List<?> comments, List<?> inlineComments) {
        this.data = data;
        this.comments = comments;
        this.inlineComments = inlineComments;
    }
}