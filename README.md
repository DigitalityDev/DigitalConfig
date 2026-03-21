# DigitalConfig

### Flexible multi-format configuration library for Java

If you like our project, please join our [Discord](https://discord.com/invite/rvWmR4scc)!

## Table of Contents

* [0. Features](#0-features)
* [1. Installation](#1-installation)
    + [Maven](#maven)
    + [Gradle](#gradle)
* [2. Quick Start](#2-quick-start)
* [3. Supported Formats](#3-supported-formats)
* [4. Reading Values](#4-reading-values)
* [5. Sections](#5-sections)
* [6. Default Config from Resources](#6-default-config-from-resources)
* [7. Custom Object Serialization](#7-custom-object-serialization)
* [8. Putting it all together](#8-putting-it-all-together)

## 0. Features

* Supports YAML, JSON, TOML and Properties formats
* Automatic format detection from file extension
* Nested key access using dot notation
* Comment preservation in YAML and `.properties` files
* Custom object serialization via `ConfigurationSerializable`
* Default config creation from classpath resources
* Bukkit/Spigot compatibility

## 1. Installation

### Maven

```xml
<repositories>
    <repository>
        <id>digitality-repo-releases</id>
        <url>https://repo.gold-zone.cz/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.digitality</groupId>
        <artifactId>digitalconfig</artifactId>
        <version>1.3.0</version>
    </dependency>
</dependencies>
```

### Gradle

```groovy
repositories {
    maven {
        name "digitalityRepoReleases"
        url "https://repo.gold-zone.cz/releases"
    }
}

dependencies {
    implementation "dev.digitality:digitalconfig:1.3.0"
}
```

## 2. Quick Start

Creating a `Configuration` is as simple as passing the file path. The format is inferred automatically from the file extension.

```java
Configuration config = new Configuration("config.yml");
config.createDefault(); // copies default from resources if file is empty

String host = config.getString("database.host");
config.set("database.host", "localhost");
config.save();
```

## 3. Supported Formats

| Format     | Extensions                       | Comment support |
|------------|----------------------------------|-----------------|
| YAML       | `.yml`, `.yaml`                  | ✅              |
| JSON       | `.json`                          | ❌              |
| TOML       | `.toml`                          | ❌              |
| Properties | `.properties`, `.prop`, `.props` | ✅              |

You can also specify the format manually instead of relying on the extension.

```java
Configuration config = new Configuration("config.txt", ConfigFormat.YAML.getFormat());
```

## 4. Reading Values

```java
config.getString("key");
config.getBoolean("key");
config.getInt("key");
config.getLong("key");
config.getFloat("key");
config.getDouble("key");
config.getStringList("key");
config.getSection("section");   // Returns a nested ConfigurationSection
config.getKeys();               // Returns all top-level keys
config.hasKey("key");           // Check if a key exists
```

Nested keys are accessed using dot notation.

```java
config.getString("server.address.host");
```

## 5. Sections

You can work with nested sections directly by calling `getSection()`.

```java
ConfigurationSection db = config.getSection("database");
String host = db.getString("host");
int port = db.getInt("port");
```

## 6. Default Config from Resources

Place a default config file in your resources folder (e.g. `src/main/resources/config.yml`), then call `createDefault()`. The file will only be written if the config file is empty or does not yet exist.

```java
config.createDefault();                      // uses the same filename as the config path
config.createDefault("defaults/config.yml"); // or specify a custom resource path
```

## 7. Custom Object Serialization

You can serialize your own objects into config files by implementing `ConfigurationSerializable`. Your class must also provide a static `deserialize` method, a static `valueOf` method, or a constructor accepting a `Map<String, Object>`.

```java
@SerializableAs("MyPoint")
public class Point implements ConfigurationSerializable {

    private final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("x", x);
        map.put("y", y);
        return map;
    }

    public static Point deserialize(Map<String, Object> args) {
        return new Point((int) args.get("x"), (int) args.get("y"));
    }
}
```

Register the class before loading any config.

```java
ConfigurationSerialization.registerClass(Point.class);
```

You can also use `@DelegateDeserialization` to delegate deserialization to another class.

```java
@DelegateDeserialization(Point.class)
public class Point3D extends Point { ... }
```

## 8. Putting it all together

Here is a final example which you can use as a reference.

```java
ConfigurationSerialization.registerClass(Point.class);

Configuration config = new Configuration("config.yml");
config.createDefault();

// Read
String host = config.getString("server.host");
int port = config.getInt("server.port");
boolean debug = config.getBoolean("server.debug");

ConfigurationSection db = config.getSection("database");
String dbHost = db.getString("host");

// Write
config.set("server.host", "localhost");
config.set("server.port", 25565);
config.set("server.debug", false);
config.set("spawn", new Point(0, 64));

config.save();
```

---

## Authors

* **Digitality** — [Discord](https://discord.com/invite/rvWmR4scc)
* **eRHaDev** — [GitHub](https://github.com/erhadev)

## License

This project is licensed under the MIT License.

> Portions of the serialization API (`ConfigurationSerializable`, `SerializableAs`, `DelegateDeserialization`) are derived from [Bukkit](https://hub.spigotmc.org/stash/projects/SPIGOT), all credits go to SpigotMC.
