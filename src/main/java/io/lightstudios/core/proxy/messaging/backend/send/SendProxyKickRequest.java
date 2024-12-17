package io.lightstudios.core.proxy.messaging.backend.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.entity.Player;

public class SendProxyKickRequest {

    public static void sendProxyPing(Player player, String kickMessage) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.CHECK_PROXY_REQUEST.getId());
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(LightCore.instance.getColorTranslation().adventureTranslator(kickMessage, player));

        player.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }

}
