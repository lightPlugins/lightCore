package io.lightstudios.core.player;

import io.lightstudios.core.util.LightTimers;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class SoundSender {

    public void onSuccess(Player player, float[] pitches, float[] volumes) {

        AtomicInteger pitchIndex = new AtomicInteger(0);
        AtomicInteger volumeIndex = new AtomicInteger(0);

        LightTimers.startTask((bukkitTask) -> {

            if (pitchIndex.get() >= pitches.length) {
                bukkitTask.cancel();
                return;
            }

            if(volumeIndex.get() >= volumes.length) {
                bukkitTask.cancel();
            }

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER,
                    volumes[volumeIndex.getAndIncrement()], pitches[pitchIndex.getAndIncrement()]);

        }, 0, 3);
    }
}
