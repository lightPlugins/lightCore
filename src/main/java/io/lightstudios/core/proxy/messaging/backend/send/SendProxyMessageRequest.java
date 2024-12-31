package io.lightstudios.core.proxy.messaging.backend.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.entity.Player;

public class SendProxyMessageRequest {

    public static void sendProxyMessage(Player player, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.MESSAGE_REQUEST.getId());
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(LightCore.instance.getColorTranslation().adventureTranslator(message, player));

        player.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }
}
