package io.lightstudios.core.hooks.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;

public class HologramManager {

    public de.oliver.fancyholograms.api.HologramManager getHoloAPI() {
        return FancyHologramsPlugin.get().getHologramManager();
    }

}
