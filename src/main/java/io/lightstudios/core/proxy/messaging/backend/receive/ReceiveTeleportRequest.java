package io.lightstudios.core.proxy.messaging.backend.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static io.lightstudios.core.LightCoreProxy.IDENTIFIER;


public class ReceiveTeleportRequest implements PluginMessageListener {

    //  70.5 76.0 527.5 10 15

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {

        if(!channel.equals("lightstudio:lightcore")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.TELEPORT_REQUEST.getId())) {
            return;
        }
        String serverName = input.readUTF();

        if(!serverName.equalsIgnoreCase(LightCore.instance.getSettings().serverName())) {
            LightCore.instance.getConsolePrinter().printInfo("Received teleport request but for another server: " + serverName);
            return;
        }

        String uuid = input.readUTF();
        String world = input.readUTF();
        double x = input.readDouble();
        double y = input.readDouble();
        double z = input.readDouble();
        float yaw = input.readFloat();
        float pitch = input.readFloat();

        LightCore.instance.getConsolePrinter().printInfo(List.of(
                "SubChannel: " + subChannel,
                "ServerName: " + serverName,
                "UUID: " + uuid,
                "World: " + world,
                "X: " + x,
                "Y: " + y,
                "Z: " + z,
                "Yaw: " + yaw,
                "Pitch: " + pitch
        ));

        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        LightCore.instance.getTeleportRequests().put(UUID.fromString(uuid), location);

    }
}
