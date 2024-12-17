package io.lightstudios.core.proxy.messaging.proxy.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import io.lightstudios.core.LightCoreProxy;
import io.lightstudios.core.proxy.util.SubChannels;
import net.kyori.adventure.text.Component;

import java.util.UUID;

import static io.lightstudios.core.LightCoreProxy.IDENTIFIER;

public class ReceiveBackendKickRequest {


    @Subscribe
    public void onPluginMessageFromPlugin(PluginMessageEvent event) {

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("Identifier does not match!");
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("Source is not an instance of ServerConnection!");
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.KICK_REQUEST.getId())) {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("SubChannel does not match!");
            return;
        }

        UUID uuid = UUID.fromString(input.readUTF());
        Component text = Component.text(input.readUTF());

        LightCoreProxy.instance.getServer().getPlayer(uuid).ifPresent(target -> target.disconnect(text));

    }
}
