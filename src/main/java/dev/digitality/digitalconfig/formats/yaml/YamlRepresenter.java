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
        this.multiRepresenters.remove(Enum.class);
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

                key.setBlockComments((List<CommentLine>) entry.getValue().getComments());
                if (value instanceof ScalarNode)
                    value.setInLineComments((List<CommentLine>) entry.getValue().getInlineComments());
                else
                    key.setInLineComments((List<CommentLine>) entry.getValue().getInlineComments());

                nodeTuples.add(new NodeTuple(key, value));
            }

            MappingNode node = new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
            node.setBlockComments((List<CommentLine>) section.getHeaderComments());
            node.setEndComments((List<CommentLine>) section.getFooterComments());

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