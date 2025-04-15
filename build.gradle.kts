import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "dev.digitality"
description = "DigitalConfig"
version = "1.2.0"

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.ADOPTIUM
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation("org.yaml:snakeyaml:2.4")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("org.codejive:java-properties:0.0.6")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        minimize {
            exclude(dependency("org.yaml:snakeyaml"))
        }

        relocate("org.yaml.snakeyaml", "dev.digitality.digitalconfig.formats.yaml.snakeyaml")
        relocate("com.google.gson", "dev.digitality.digitalconfig.formats.json.gson")
        relocate("org.codejive.properties", "dev.digitality.digitalconfig.formats.properties.java_properties")
        relocate("com.moandjiezana.toml", "dev.digitality.digitalconfig.formats.toml.toml4j")

        archiveFileName = "${project.name}.jar"
    }

    register("sourceJar", Jar::class) {
        archiveClassifier = "sources"

        from(sourceSets.main.get().allSource)
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.gold-zone.cz/private")

            credentials {
                username = (project.findProperty("goldzoneRepo.username") ?: System.getenv("GOLDZONE_REPO_USERNAME")) as String?
                password = (project.findProperty("goldzoneRepo.password") ?: System.getenv("GOLDZONE_REPO_PASSWORD")) as String?
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("shadow") {
            groupId = project.group as String?
            artifactId = project.name.lowercase()
            version = project.version as String?

            artifact(tasks["sourceJar"])

            from(components["shadow"])
        }
    }
}