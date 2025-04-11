package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.serialization.ConfigurationSerializable;
import dev.digitality.digitalconfig.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlRepresenter extends Representer {
    public YamlRepresenter(DumperOptions options) {
        super(options);

        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
        try {
            this.multiRepresenters.put(Class.forName("org.bukkit.configuration.serialization.ConfigurationSerializable"), new RepresentConfigurationSerializable());
        } catch (ClassNotFoundException ignored) {}
    }

    public Node representObject(Object data) {
        return super.representData(data);
    }

    private class RepresentConfigurationSection extends RepresentMap {
        @Override
        public Node representData(Object data) {
            ConfigurationSection section = (ConfigurationSection) data;
            List<NodeTuple> nodeTuples = new ArrayList<>();

            for (Map.Entry<String, ConfigurationPath> entry : section.getData().entrySet()) {
                Node key = representObject(entry.getKey());
                Node value = representObject(entry.getValue().getData());

                if (entry.getValue().getMetadata().containsKey("yaml.block_comments"))
                    key.setBlockComments((List<CommentLine>) entry.getValue().getMetadata().get("yaml.block_comments"));

                if (entry.getValue().getMetadata().containsKey("yaml.inline_comments")) {
                    List<CommentLine> inlineComments = (List<CommentLine>) entry.getValue().getMetadata().get("yaml.inline_comments");

                    if (value instanceof ScalarNode)
                        value.setInLineComments(inlineComments);
                    else
                        key.setInLineComments(inlineComments);
                }

                nodeTuples.add(new NodeTuple(key, value));
            }

            MappingNode node = new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
            if (section.getMetadata().containsKey("yaml.header_comments"))
                node.setBlockComments((List<CommentLine>) section.getMetadata().get("yaml.header_comments"));
            if (section.getMetadata().containsKey("yaml.footer_comments"))
                node.setEndComments((List<CommentLine>) section.getMetadata().get("yaml.footer_comments"));

            return node;
        }
    }

    private class RepresentConfigurationSerializable extends RepresentMap {
        @Override
        public Node representData(Object data) {
            return super.representData(ConfigurationSerialization.serialize(data));
        }
    }
}