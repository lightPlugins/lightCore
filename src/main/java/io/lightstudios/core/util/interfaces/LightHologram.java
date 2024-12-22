package io.lightstudios.core.util.interfaces;

import org.bukkit.Color;
import org.bukkit.Location;

import java.util.List;

public interface LightHologram {

    Location location();
    List<String> lines();
    Color backgroundColor();
    boolean enableShadow();

}
