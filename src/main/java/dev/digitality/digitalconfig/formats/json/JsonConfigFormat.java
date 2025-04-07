package dev.digitality.digitalconfig.formats.json;

import com.google.gson.*;
import dev.digitality.digitalconfig.config.ConfigurationPath;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import dev.digitality.digitalconfig.formats.IConfigFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonConfigFormat implements IConfigFormat {
    private final Gson gson = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY.withIndent(" ".repeat(4))).create();

    @Override
    public ConfigurationSection deserialize(String content) {
        JsonObject jsonObject = gson.fromJson(content, JsonObject.class);

        return fromJsonObject(jsonObject);
    }

    @Override
    public String serialize(ConfigurationSection section) {
        Map<String, Object> map = toMap(section);

        return gson.toJson(map);
    }

    private Map<String, Object> toMap(ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String, ConfigurationPath> entry : section.getData().entrySet()) {
            Object data = entry.getValue().getData();

            if (data instanceof ConfigurationSection) {
                map.put(entry.getKey(), toMap((ConfigurationSection) data));
            } else if (data instanceof List) {
                map.put(entry.getKey(), toMap((List<Object>) data));
            } else {
                map.put(entry.getKey(), data);
            }
        }

        return map;
    }

    private List<Object> toMap(List<Object> oldList) {
        List<Object> list = new ArrayList<>();

        for (Object item : oldList) {
            if (item instanceof ConfigurationSection) {
                list.add(toMap((ConfigurationSection) item));
            } else if (item instanceof List) {
                list.addAll(toMap((List<Object>) item));
            } else {
                list.add(item);
            }
        }

        return list;
    }

    private ConfigurationSection fromJsonObject(JsonObject jsonObject) {
        ConfigurationSection section = new ConfigurationSection();

        for (Map.Entry<String, JsonElement> entry : jsonObject.asMap().entrySet()) {
            JsonElement element = entry.getValue();

            section.getData().put(entry.getKey(), new ConfigurationPath(fromJsonElement(element)));
        }

        return section;
    }

    private List<Object> fromJsonArray(JsonArray jsonArray) {
        List<Object> list = new ArrayList<>();

        for (JsonElement arrayElement : jsonArray.asList()) {
            list.add(fromJsonElement(arrayElement));
        }

        return list;
    }

    private Object fromJsonElement(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            return fromJsonObject(jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonArray()) {
            return fromJsonArray(jsonElement.getAsJsonArray());
        } else if (jsonElement.isJsonPrimitive()) {
            if (jsonElement.getAsJsonPrimitive().isString()) {
                return jsonElement.getAsString();
            } else if (jsonElement.getAsJsonPrimitive().isNumber()) {
                return jsonElement.getAsNumber();
            } else if (jsonElement.getAsJsonPrimitive().isBoolean()) {
                return jsonElement.getAsBoolean();
            }
        } else if (jsonElement.isJsonNull()) {
            return null;
        }

        return null;
    }
}
