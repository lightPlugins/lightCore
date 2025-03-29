package io.lightstudios.core.github;

import io.lightstudios.core.LightCore;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class GithubVersionManager {

    private final String GITHUB_API_URL;
    private final String currentVersion;

    /**
     * Create a new GithubVersionManager instance with the plugin name and the current version.
     * @param pluginName the name of the plugin (Case sensitive)
     * @param currentVersion the current version of the plugin
     */
    public GithubVersionManager(String pluginName, String currentVersion) {
        this.currentVersion = currentVersion;
        this.GITHUB_API_URL = "https://api.github.com/repos/lightPlugins/" + pluginName + "/releases/latest";
    }

    public CompletableFuture<String> getLatestVersionAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URI uri = new URI(GITHUB_API_URL);
                URL url = uri.toURL();
                JSONObject jsonResponse = getJsonObject(url);
                return jsonResponse.getString("tag_name");
            } catch (Exception e) {
                LightCore.instance.getConsolePrinter().printError("Could not check for plugin updates: " + e.getMessage());
                return null;
            }
        });
    }

    private @NotNull JSONObject getJsonObject(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return new JSONObject(response.toString());
    }

    public CompletableFuture<UpdateCheckResult> isUpdateAvailableAsync() {
        return getLatestVersionAsync().thenApply(latestVersion -> {
            boolean updateAvailable = latestVersion != null && !latestVersion.equals(currentVersion);
            return new UpdateCheckResult(updateAvailable, latestVersion);
        });
    }

    public record UpdateCheckResult(boolean updateAvailable, String latestVersion) {

    }
}