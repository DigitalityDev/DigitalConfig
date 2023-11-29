package dev.digitality.digitalconfig.config;

import lombok.Data;
import org.yaml.snakeyaml.comments.CommentLine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class ConfigurationPath {
    private Object data;
    private List<CommentLine> comments;
    private List<CommentLine> inlineComments;

    public ConfigurationPath(Object data) {
        this.data = data;
        this.comments = new ArrayList<>();
        this.inlineComments = new ArrayList<>();
    }

    public ConfigurationPath(Object data, List<CommentLine> comments, List<CommentLine> inlineComments) {
        this.data = data;
        this.comments = comments;
        this.inlineComments = inlineComments;
    }
}