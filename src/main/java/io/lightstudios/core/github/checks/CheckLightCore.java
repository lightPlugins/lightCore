package io.lightstudios.core.github.checks;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.github.GithubVersionManager;
import lombok.Getter;

@Getter
public class CheckLightCore {

    private final String currentVersion;
    private final GithubVersionManager versionManager;
    private final String pluginName = "lightCore";

    public CheckLightCore() {
        this.currentVersion = LightCore.instance.getDescription().getVersion();
        this.versionManager = new GithubVersionManager(pluginName, currentVersion);
    }
}
