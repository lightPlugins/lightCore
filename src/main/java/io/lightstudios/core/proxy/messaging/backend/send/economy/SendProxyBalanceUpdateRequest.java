package io.lightstudios.core.proxy.messaging.backend.send.economy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

public class SendProxyBalanceUpdateRequest {

    public static void sendBalanceUpdateThrowProxy(Player player, UUID uuid, BigDecimal newBalance) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.BALANCE_UPDATE_REQUEST.getId());
        out.writeUTF(uuid.toString());
        out.writeDouble(newBalance.doubleValue());

        player.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }

}
