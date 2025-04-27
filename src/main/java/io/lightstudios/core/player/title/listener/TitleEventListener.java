package io.lightstudios.core.player.title.listener;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.player.title.events.TitleSendEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.*;

public class TitleEventListener implements Listener {

    private final Map<UUID, Queue<Title>> titleQueues = new HashMap<>();

    @EventHandler
    public void onTitleSend(TitleSendEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Title title = event.getTitle();

        // Füge den Titel in die Warteschlange des Spielers ein
        titleQueues.computeIfAbsent(playerId, k -> new LinkedList<>()).add(title);

        // Starte die Verarbeitung der Warteschlange, falls sie nicht bereits läuft
        if (titleQueues.get(playerId).size() == 1) {
            processQueue(player);
        }
    }

    private void processQueue(Player player) {
        UUID playerId = player.getUniqueId();
        Queue<Title> queue = titleQueues.get(playerId);

        if (queue == null || queue.isEmpty()) {
            return;
        }

        Title title = queue.poll();
        if (title == null) {
            return;
        }

        // Zeige den Titel an
        player.showTitle(title);

        // Berechne die Gesamtdauer des Titels (fadeIn + stay + fadeOut) + 1 Sekunde Verzögerung
        Title.Times times = title.times();
        Duration totalDuration = Duration.ZERO;

        if (times != null) {
            totalDuration = Duration.ofMillis(times.fadeIn().toMillis())
                    .plus(Duration.ofMillis(times.stay().toMillis()))
                    .plus(Duration.ofMillis(times.fadeOut().toMillis()))
                    .plus(Duration.ofSeconds(1));
        }

        // Plane die Verarbeitung des nächsten Titels nach der Gesamtdauer
        Bukkit.getScheduler().runTaskLater(
                LightCore.instance,
                () -> processQueue(player),
                totalDuration.toMillis() / 50 // Konvertiere Millisekunden in Ticks (1 Tick = 50ms)
        );
    }
}