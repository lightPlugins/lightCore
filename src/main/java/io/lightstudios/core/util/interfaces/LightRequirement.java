package io.lightstudios.core.util.interfaces;

import org.bukkit.entity.Player;

public interface LightRequirement {
    boolean checkRequirement(Player player, String[] requirementDataArray);
}
