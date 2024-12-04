package io.lightstudios.core.proxy.channels;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.lightstudios.core.LightCoreProxy;
import net.kyori.adventure.text.Component;

import java.util.Optional;

import static io.lightstudios.core.LightCoreProxy.IDENTIFIER;

public class ReceivePlayerTeleport {

    @Subscribe
    public void onPluginMessageFromPlayer(PluginMessageEvent event) {

        if (!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        String subChannel = in.readUTF();

        if(!subChannel.equalsIgnoreCase("teleport")) {
            return;
        }

        String serverName = in.readUTF();
        String worldName = in.readUTF();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = in.readFloat();
        float pitch = in.readFloat();

        // Retrieve the player from the event
        if (event.getSource() instanceof Player player) {

            // Find the target server
            Optional<RegisteredServer> targetServer = LightCoreProxy.instance.getServer().getServer(serverName);

            if (targetServer.isPresent()) {
                // Send the player to the target server
                player.createConnectionRequest(targetServer.get()).fireAndForget();

                // Send a plugin message to the target server to set the player's location
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("teleport");
                out.writeUTF(player.getUniqueId().toString());
                out.writeUTF(worldName);
                out.writeDouble(x);
                out.writeDouble(y);
                out.writeDouble(z);
                out.writeFloat(yaw);
                out.writeFloat(pitch);

                targetServer.get().sendPluginMessage(IDENTIFIER, out.toByteArray());
            } else {
                player.sendMessage(Component.text("Failed to connect to the target server."));
            }
        }
    }
}
