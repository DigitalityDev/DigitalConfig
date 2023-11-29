package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlRepresenter extends Representer {
    public YamlRepresenter(DumperOptions options) {
        super(options);

        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
//        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
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

                key.setBlockComments(entry.getValue().getComments());
                if (value instanceof ScalarNode)
                    value.setInLineComments(entry.getValue().getInlineComments());
                else
                    key.setInLineComments(entry.getValue().getInlineComments());

                nodeTuples.add(new NodeTuple(key, value));
            }

            MappingNode node = new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
            node.setBlockComments(section.getHeaderComments());
            node.setEndComments(section.getFooterComments());

            return node;
        }
    }

//    private class RepresentConfigurationSerializable extends RepresentMap {
//        @Override
//        public Node representData(@NotNull Object data) {
//            ConfigurationSerializable serializable = (ConfigurationSerializable) data;
//            Map<String, Object> values = new LinkedHashMap<String, Object>();
//            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
//            values.putAll(serializable.serialize());
//
//            return super.representData(values);
//        }
//    }
}