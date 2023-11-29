package dev.digitality.digitalconfig.formats.yaml;

import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.*;

public class YamlConstructor extends SafeConstructor {
    public YamlConstructor(LoaderOptions loaderOptions) {
        super(loaderOptions);

        this.yamlConstructors.put(Tag.MAP, new ConstructConfigurationSection());
    }

    @Override
    public void flattenMapping(MappingNode node) {
        super.flattenMapping(node);
    }

    @Override
    public Object constructObject(Node node) {
        return super.constructObject(node);
    }

    private class ConstructConfigurationSection extends ConstructYamlMap {
        @Override
        public ConfigurationSection construct(Node node) {
            if (node.isTwoStepsConstruction())
                throw new YAMLException("Unexpected referential mapping structure. Node: " + node);

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

//            if (raw.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
//                Map<String, Object> typed = new LinkedHashMap<String, Object>(raw.size());
//                for (Map.Entry<?, ?> entry : raw.entrySet()) {
//                    typed.put(entry.getKey().toString(), entry.getValue());
//                }
//
//                try {
//                    return ConfigurationSerialization.deserializeObject(typed);
//                } catch (IllegalArgumentException ex) {
//                    throw new YAMLException("Could not deserialize object", ex);
//                }
//            }

            return root;
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
        }
    }
}