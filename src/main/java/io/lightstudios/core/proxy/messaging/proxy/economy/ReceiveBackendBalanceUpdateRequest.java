package io.lightstudios.core.proxy.messaging.proxy.economy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.lightstudios.core.LightCoreProxy;
import io.lightstudios.core.proxy.util.SubChannels;

import java.math.BigDecimal;

import static io.lightstudios.core.LightCoreProxy.IDENTIFIER;

public class ReceiveBackendBalanceUpdateRequest {

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.TELEPORT_REQUEST.getId())) {
            return;
        }

        String uuid = input.readUTF();
        double newBalance = input.readDouble();
        BigDecimal balance = BigDecimal.valueOf(newBalance);

        Player target = LightCoreProxy.instance.getServer().getAllPlayers().stream().filter(p
                -> p.getUniqueId().toString().equals(uuid)).findFirst().orElse(null);

        if(target == null) {
            return;
        }

        ServerConnection connection = target.getCurrentServer().orElse(null);

        if(connection == null) {
            return;
        }

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(SubChannels.BALANCE_UPDATE_REQUEST.getId());
        output.writeUTF(uuid);
        output.writeDouble(balance.doubleValue());

        connection.getServer().sendPluginMessage(IDENTIFIER, output.toByteArray());

    }
}
