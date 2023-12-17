package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class YamlConstructor extends SafeConstructor {
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
                typed.put(String.valueOf(entry.getKey()), entry.getValue());
            }

            return ConfigurationSerialization.deserialize(typed);
        }

        private ConfigurationSection constructConfigurationSection(Node node) {
            MappingNode rootNode = (MappingNode) node;
            ConfigurationSection root = new ConfigurationSection();
            root.setHeaderComments(rootNode.getBlockComments());
            root.setFooterComments(rootNode.getEndComments());

            flattenMapping(rootNode);
            for (NodeTuple nodeTuple : rootNode.getValue()) {
                Node key = nodeTuple.getKeyNode();
                Node value = nodeTuple.getValueNode();

                while (value instanceof AnchorNode)
                    value = ((AnchorNode) value).getRealNode();

                ConfigurationPath path = new ConfigurationPath(constructObject(value), key.getBlockComments(), value instanceof ScalarNode ? value.getInLineComments() : key.getInLineComments());
                root.set(String.valueOf(constructObject(key)), path);
            }

            return root;
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
        }
    }
}