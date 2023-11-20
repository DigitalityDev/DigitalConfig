package dev.digitality.digitalconfig.formats;

import lombok.Cleanup;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class YamlConfigFormat implements ConfigFormat {
    private final Yaml yaml;

    public YamlConfigFormat() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setProcessComments(true);

        this.yaml = new Yaml(dumperOptions);
    }

    @Override
    public HashMap<String, Object> load(String content) {
        @Cleanup
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        @Cleanup
        Reader reader = new UnicodeReader(input);
        Node rootNode = yaml.compose(reader);

        if (!(rootNode instanceof MappingNode))
            throw new IllegalArgumentException("Root node must be a mapping node");


    }

    @Override
    public void save(HashMap<String, Object> config) {

    }
}
