package io.lightstudios.core.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LightSounds {

    public static void onSuccess(Player player) {
        Sound sound = Sound.ENTITY_PLAYER_LEVELUP;
        float volume = 0.75f;
        float pitch = 1.6f;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void onFail(Player player) {
        Sound sound = Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;
        float volume = 0.75f;
        float pitch = 0.1f;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void onAttention(Player player) {
        Sound sound = Sound.BLOCK_AMETHYST_CLUSTER_BREAK;
        float volume = 0.75f;
        float pitch = 0.8f;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
