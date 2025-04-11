package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.config.ConfigurationValue;
import dev.digitality.digitalconfig.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class YamlConstructor extends Constructor {
    public YamlConstructor(LoaderOptions loaderOptions) {
        super(loaderOptions);

        this.yamlConstructors.put(Tag.MAP, new ConstructMappingNode());
    }

    @Override
    public void flattenMapping(MappingNode node) {
        super.flattenMapping(node);
    }

    @Override
    public Object constructObject(Node node) {
        return super.constructObject(node);
    }

    private class ConstructMappingNode extends ConstructYamlMap {
        @Override
        public Object construct(Node node) {
            if (node.isTwoStepsConstruction())
                throw new YAMLException("Unexpected referential mapping structure. Node: " + node);

            return constructSerializable(node)
                    .orElseGet(() -> constructConfigurationSection(node));
        }

        private Optional<Object> constructSerializable(Node node) {
            Map<?, ?> raw = (Map<?, ?>) super.construct(node);

            Map<String, Object> typed = new LinkedHashMap<>(raw.size());
            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection section) {
                    typed.put(String.valueOf(entry.getKey()), unwrapSection(section));
                } else {
                    typed.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }

            return ConfigurationSerialization.deserialize(typed);
        }

        private Map<String, Object> unwrapSection(ConfigurationSection section) {
            Map<String, Object> map = new LinkedHashMap<>();

            for (Map.Entry<String, ConfigurationValue> entry : section.getData().entrySet()) {
                if (entry.getValue().getData() instanceof ConfigurationSection)
                    map.put(entry.getKey(), unwrapSection((ConfigurationSection) entry.getValue().getData()));
                else
                    map.put(entry.getKey(), entry.getValue().getData());
            }

            return map;
        }

        private ConfigurationSection constructConfigurationSection(Node node) {
            MappingNode rootNode = (MappingNode) node;
            ConfigurationSection root = new ConfigurationSection();
            root.getMetadata().put("yaml.header_comments", rootNode.getBlockComments());
            root.getMetadata().put("yaml.footer_comments", rootNode.getEndComments());

            flattenMapping(rootNode);
            for (NodeTuple nodeTuple : rootNode.getValue()) {
                Node key = nodeTuple.getKeyNode();
                Node value = nodeTuple.getValueNode();

                while (value instanceof AnchorNode)
                    value = ((AnchorNode) value).getRealNode();

                ConfigurationValue path = new ConfigurationValue(constructObject(value));
                path.getMetadata().put("yaml.block_comments", key.getBlockComments());
                path.getMetadata().put("yaml.inline_comments", value instanceof ScalarNode ? value.getInLineComments() : key.getInLineComments());

                root.getData().put(String.valueOf(constructObject(key)), path);
            }

            return root;
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
        }
    }
}