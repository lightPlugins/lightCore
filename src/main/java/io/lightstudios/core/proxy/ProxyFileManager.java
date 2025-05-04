package io.lightstudios.core.proxy;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ProxyFileManager {

    private final String configName;
    private final boolean loadDefaultsOnReload;

    private final Path configFilePath;
    private Map<String, Object> configData;
    private Map<String, Object> defaultConfigData;

    private final Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));

    public ProxyFileManager(Path dataDirectory, String configName, boolean loadDefaultsOnReload) {
        this.configName = configName;
        this.loadDefaultsOnReload = loadDefaultsOnReload;

        this.configFilePath = dataDirectory.resolve(configName);

        try {
            // Stelle sicher, dass das Verzeichnis existiert
            Path parentDir = this.configFilePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Konnte Verzeichnis für Config nicht erstellen: " + configFilePath, e);
        }

        loadDefaultConfig();
    }

    /**
     * Lädt oder erstellt die Konfigurationsdatei mit Standardwerten.
     */
    private void loadDefaultConfig() {
        try {
            if (!Files.exists(configFilePath)) {
                // Dynamisch Ressourcenpfad lesen (auch bei Unterverzeichnissen)
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(configName)) {
                    if (in == null) throw new FileNotFoundException("Default config '" + configName + "' not found in resources!");
                    Files.copy(in, configFilePath);
                }
            }

            reloadConfig();

            if (loadDefaultsOnReload) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(configName)) {
                    if (in != null) {
                        defaultConfigData = yaml.load(in);
                        mergeMissingKeys(configData, defaultConfigData, "");
                        saveConfig();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load config file: " + configName, e);
        }
    }

    public void reloadConfig() {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configFilePath))) {
            configData = yaml.load(reader);
            if (configData == null) configData = new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Could not reload config file: " + configName, e);
        }
    }

    public void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(configFilePath)) {
            yaml.dump(configData, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save config file: " + configName, e);
        }
    }

    public Map<String, Object> getConfig() {
        return configData;
    }

    private void mergeMissingKeys(Map<String, Object> target, Map<String, Object> defaults, String path) {
        for (String key : defaults.keySet()) {
            Object defaultValue = defaults.get(key);
            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (!target.containsKey(key)) {
                System.out.println("[FileManager] Added missing config key: " + fullPath);
                target.put(key, defaultValue);
            } else if (defaultValue instanceof Map && target.get(key) instanceof Map) {
                mergeMissingKeys(
                        (Map<String, Object>) target.get(key),
                        (Map<String, Object>) defaultValue,
                        fullPath
                );
            }
        }
    }

    public Object get(String key) {
        return resolveKey(configData, key);
    }

    public void set(String key, Object value) {
        setKey(configData, key, value);
    }

    @SuppressWarnings("unchecked")
    private Object resolveKey(Map<String, Object> section, String path) {
        String[] keys = path.split("\\.");
        Object current = section;

        for (String key : keys) {
            if (!(current instanceof Map)) return null;
            current = ((Map<String, Object>) current).get(key);
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private void setKey(Map<String, Object> section, String path, Object value) {
        String[] keys = path.split("\\.");
        Map<String, Object> current = section;

        for (int i = 0; i < keys.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(keys[i], k -> new HashMap<>());
        }

        current.put(keys[keys.length - 1], value);
    }
}
