package io.lightstudios.core.util.namespace;

import lombok.Getter;
import org.bukkit.NamespacedKey;

@Getter
public enum LightNamespaceKey {

    IS_PLAYER_PLACED(new NamespacedKey("lightcore", "is_player_placed"));

    private final NamespacedKey key;

    LightNamespaceKey(NamespacedKey key) {
        this.key = key;
    }
}
