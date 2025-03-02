package io.lightstudios.core.github.checks;

import io.lightstudios.coins.LightCoins;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.github.GithubVersionManager;
import lombok.Getter;

@Getter
public class CheckLightCoins {

    private final String currentVersion;
    private final String pluginName = "lightCoins";
    private final GithubVersionManager versionManager;

    public CheckLightCoins() {
        if (LightCore.instance.getHookManager().isExistLightCoins()) {
            LightCoins lightCoins = LightCoins.instance;
            this.currentVersion = lightCoins.getDescription().getVersion();
            this.versionManager = new GithubVersionManager(pluginName, currentVersion);
        } else {
            this.currentVersion = null;
            this.versionManager = null;
        }
    }
}
