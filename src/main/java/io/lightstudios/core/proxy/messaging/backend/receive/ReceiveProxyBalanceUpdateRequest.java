package io.lightstudios.core.proxy.messaging.backend.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ReceiveProxyBalanceUpdateRequest implements PluginMessageListener {


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

        String uuidString = input.readUTF();
        UUID uuid = UUID.fromString(uuidString);

        double balance = input.readDouble();
        BigDecimal bigDecimal = BigDecimal.valueOf(balance);

        LightCore.instance.getHookManager().getLightCoinsManager().setRawBalance(uuid, bigDecimal);
        LightCore.instance.getConsolePrinter().printInfo("Received proxy balance update request for " + uuid + " with balance " + bigDecimal);


    }
}
