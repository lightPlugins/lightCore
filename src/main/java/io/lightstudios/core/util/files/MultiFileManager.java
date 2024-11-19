package io.lightstudios.core.util.files;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

    /*
     *
     * Multi-Configuration-Manager by lightStudios © 2024
     * This class is used to manage the configuration files of your plugin.
     *
     *  LICENSE: MIT
     *  AUTHOR: lightStudios
     *  VERSION: 1.0
     *  DATE: 2024
     */

public class MultiFileManager {

    private final String directoryPath;
    @Getter
    private List<File> yamlFiles;

    public MultiFileManager(String directoryPath) throws IOException {
        this.directoryPath = directoryPath;
        loadYmlFiles();
    }

    /**
     * Load all yml files found in the specified directory
     * Save all found files in the "List<File> yamlFiles" field
     */
    private void loadYmlFiles() throws IOException {
        yamlFiles = new ArrayList<>();
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".yml")) {
                    yamlFiles.add(file.toFile());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Reload all yml files found in the specified directory
     */
    public void reload() throws IOException {
        loadYmlFiles();
    }

    /**
     * Get the file name without the extension (any extension)
     */
    public String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }
}
