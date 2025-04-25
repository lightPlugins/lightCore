package io.lightstudios.core.actions.types;

import io.lightstudios.core.util.interfaces.LightAction;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;

public class PlaySoundAction implements LightAction {
    @Override
    public void execute(Player player, String[] actionDataArray) {
        try {
            if (actionDataArray.length < 4) {
                throw new IllegalArgumentException("Ungültige Anzahl an Argumenten für PlaySoundAction.");
            }

            @Subst("test") String actionSound = actionDataArray[1];
            String namespace = "minecraft";
            Key soundKey = Key.key(namespace, actionSound);
            Sound sound = Registry.SOUNDS.get(soundKey);
            if (sound == null) {
                throw new IllegalArgumentException("Ungültiger Sound: " + actionSound);
            }

            float volume = Float.parseFloat(actionDataArray[2]);
            float pitch = Float.parseFloat(actionDataArray[3]);

            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException | NullPointerException e) {
            player.sendMessage("Fehler beim Abspielen des Sounds: " + e.getMessage());
        }
    }
}
