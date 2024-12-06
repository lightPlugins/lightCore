package io.lightstudios.core.actions.types;

import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        String actionSound = actionDataArray[1];
        Sound sound = Sound.valueOf(actionSound.toUpperCase());
        float volume = Float.parseFloat(actionDataArray[2]);
        float pitch = Float.parseFloat(actionDataArray[3]);

        player.playSound(player.getLocation(), sound, volume, pitch);

    }
}
