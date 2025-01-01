package io.lightstudios.core.proxy.messaging.backend;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.lightstudios.coins.api.models.AccountData;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import io.lightstudios.core.util.interfaces.LightMessageListener;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class BalanceUpdateRequest implements LightMessageListener {


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {

        if(!channel.equals("lightstudio:lightcore")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subChannel = input.readUTF();
        if(!subChannel.equalsIgnoreCase(SubChannels.BALANCE_UPDATE_REQUEST.getId())) {
            return;
        }
        String uuidString = input.readUTF();
        UUID uuid = UUID.fromString(uuidString);

        double balance = input.readDouble();
        BigDecimal bigDecimal = BigDecimal.valueOf(balance);
        LightCore.instance.getHookManager().getLightCoinsManager().getAPI().createAccountDataAsync(LightCore.instance, uuid, player.getName())
                .thenAccept(accountData -> {
                    AccountData data = LightCore.instance.getHookManager().getLightCoinsManager().getAPI().getAccountData(uuid);
                    if(data == null) {
                        return;
                    }
                    data.getCoinsData().setCurrentCoins(bigDecimal);
                    LightCore.instance.getConsolePrinter().printInfo("SUCCESS -> Received proxy balance update request for "
                            + uuid + " with balance " + bigDecimal);
        });



    }
}
