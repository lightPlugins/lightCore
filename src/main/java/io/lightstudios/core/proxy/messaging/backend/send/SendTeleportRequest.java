package io.lightstudios.core.proxy.messaging.backend.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.entity.Player;

public class SendTeleportRequest {

    public static void sendTeleportRequest(Player sender, String serverName, String world, double x, double y, double z, float yaw, float pitch) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.TELEPORT_REQUEST.getId());
        out.writeUTF(serverName);
        out.writeUTF(sender.getUniqueId().toString());
        out.writeUTF(world);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);

        sender.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }
}
