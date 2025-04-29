package io.lightstudios.core.player.title.listener;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.player.title.events.TitleSendEvent;
import io.lightstudios.core.proxy.messaging.backend.sender.SendProxyRequest;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

        Title title = queue.peek();
        if (title == null) {
            return;
        }

        String lower = LegacyComponentSerializer.legacySection().serialize(title.subtitle());
        String upper = LegacyComponentSerializer.legacySection().serialize(title.title());

        int fadeIn;
        int stay;
        int fadeOut;

        Title.Times times = title.times();

        if(times != null) {
            fadeIn = (int) times.fadeIn().toMillis();
            stay = (int) times.stay().toMillis();
            fadeOut = (int) times.fadeOut().toMillis();
        } else {
            upper = "<dark_red>Proxy Title Error";
            lower = "<red>Title.Times is null";
            fadeIn = 0;
            stay = 60;
            fadeOut = 0;
        }

        // Zeige den Titel an
        if(!player.isOnline()) {
            SendProxyRequest.sendProxyTitle(player, upper, lower, fadeIn, stay, fadeOut);
        } else {
            player.showTitle(title);
        }

        // Berechne die Gesamtdauer des Titels (fadeIn + stay + fadeOut) + 1 Sekunde Verzögerung
        Duration totalDuration = Duration.ZERO;

        if (times != null) {
            totalDuration = Duration.ofMillis(times.fadeIn().toMillis() * 50L)
                    .plus(Duration.ofMillis(times.stay().toMillis() * 50L))
                    .plus(Duration.ofMillis(times.fadeOut().toMillis() * 50L))
                    .plus(Duration.ofMillis(0));
        }


        // Plane die Verarbeitung des nächsten Titels nach der Gesamtdauer
        Bukkit.getScheduler().runTaskLater(
                LightCore.instance,
                () -> {
                    queue.poll();
                    processQueue(player);
                },
                totalDuration.toMillis() / 50L // Konvertiere Millisekunden in Ticks (1 Tick = 50ms)
        );
    }
}