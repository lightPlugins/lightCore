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
            if (latestVersion == null || !latestVersion.matches("\\d+\\.\\d+\\.\\d+")) {
                return new UpdateCheckResult(false, latestVersion);
            }

            String[] currentParts = currentVersion.split("\\.");
            String[] latestParts = latestVersion.split("\\.");

            int currentMajor = Integer.parseInt(currentParts[0]);
            int currentMinor = Integer.parseInt(currentParts[1]);
            int currentBugfix = Integer.parseInt(currentParts[2]);

            int latestMajor = Integer.parseInt(latestParts[0]);
            int latestMinor = Integer.parseInt(latestParts[1]);
            int latestBugfix = Integer.parseInt(latestParts[2]);

            boolean updateAvailable = (latestMajor > currentMajor) ||
                    (latestMajor == currentMajor && latestMinor > currentMinor) ||
                    (latestMajor == currentMajor && latestMinor == currentMinor && latestBugfix > currentBugfix);

            return new UpdateCheckResult(updateAvailable, latestVersion);
        });
    }

    public record UpdateCheckResult(boolean updateAvailable, String latestVersion) {

    }
}