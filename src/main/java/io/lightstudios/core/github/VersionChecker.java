package io.lightstudios.core.github;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.github.checks.CheckLightCoins;
import io.lightstudios.core.github.checks.CheckLightCore;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class VersionChecker {

    private final CheckLightCore checkLightCore;
    private final CheckLightCoins checkLightCoins;
    private final ScheduledExecutorService scheduler;

    public VersionChecker() {
        this.checkLightCore = new CheckLightCore();
        this.checkLightCoins = new CheckLightCoins();
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduleUpdateChecks();
    }

    private void scheduleUpdateChecks() {
        scheduler.scheduleAtFixedRate(this::checkForUpdates, 0, 2, TimeUnit.HOURS);
    }

    private void checkForUpdates() {
        LightCore.instance.getLogger().info("Checking for plugin updates...");

        CompletableFuture<GithubVersionManager.UpdateCheckResult> lightCore =
                this.checkLightCore.getVersionManager().isUpdateAvailableAsync();
        CompletableFuture<GithubVersionManager.UpdateCheckResult> lightCoins =
                this.checkLightCoins.getVersionManager().isUpdateAvailableAsync();

        // check for lightCore updates
        lightCore.thenAccept(result -> {
            if (result.updateAvailable()) {
                LightCore.instance.getConsolePrinter().printInfo(List.of(
                        "An update is available for " + this.checkLightCore.getPluginName() + "!",
                        "Latest version: " + result.latestVersion(),
                        "Your version: " + this.checkLightCore.getCurrentVersion(),
                        "Download it here: https://github.com/lightPlugins/lightCore/releases"
                ));
            } else {
                LightCore.instance.getConsolePrinter().printInfo(this.checkLightCore.getPluginName() + " is up to date!");
            }
        }).exceptionally(ex -> {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Failed to check for updates for " + this.checkLightCore.getPluginName() + "!",
                    "Error: " + ex.getMessage()
            ));
            return null;
        });

        // check for lightCoins updates
        lightCoins.thenAccept(result -> {
            if (result.updateAvailable()) {
                LightCore.instance.getConsolePrinter().printInfo(List.of(
                        "An update is available for " + this.checkLightCoins.getPluginName() + "!",
                        "Latest version: " + result.latestVersion(),
                        "Your version: " + this.checkLightCoins.getCurrentVersion(),
                        "Download it here: https://www.spigotmc.org/resources/83862"
                ));
            } else {
                LightCore.instance.getConsolePrinter().printInfo(this.checkLightCoins.getPluginName() + " is up to date!");
            }
        }).exceptionally(ex -> {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Failed to check for updates for " + this.checkLightCoins.getPluginName() + "!",
                    "Error: " + ex.getMessage()
            ));
            return null;
        });
    }

}