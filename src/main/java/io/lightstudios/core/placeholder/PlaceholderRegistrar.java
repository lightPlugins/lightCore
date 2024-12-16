package io.lightstudios.core.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlaceholderRegistrar extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;
    private final boolean persist;

    private final ArrayList<LightPlaceholder> lightPlaceholder;

    public PlaceholderRegistrar(
            String identifier,
            String author,
            String version,
            boolean persist,
            ArrayList<LightPlaceholder> lightPlaceholder) {

        this.identifier = identifier;
        this.author = author;
        this.version = version;
        this.persist = persist;
        this.lightPlaceholder = lightPlaceholder;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return this.author;
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

    @Override
    public boolean persist() {
        return this.persist;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        for (LightPlaceholder placeholder : lightPlaceholder) {
            return placeholder.onRequest(player, params);
        }

        return null;
    }
}
