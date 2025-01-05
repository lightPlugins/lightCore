package io.lightstudios.core.hooks.lightcoins;

import io.lightstudios.coins.LightCoins;
import io.lightstudios.coins.api.LightCoinsAPI;
import io.lightstudios.coins.api.VirtualResponse;
import io.lightstudios.coins.api.models.AccountData;
import io.lightstudios.coins.api.models.CoinsData;
import io.lightstudios.coins.api.models.VirtualData;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class LightCoinsManager {

    public LightCoinsAPI getAPI() {
        return LightCoins.instance.getLightCoinsAPI();
    }

    public AccountData getAccountData(UUID uuid) {
        return getAPI().getAccountData(uuid);
    }

    public CoinsData getCoinsData(UUID uuid) {
        return getAccountData(uuid).getCoinsData();
    }

    public List<VirtualData> getVirtualData(UUID uuid) {
        return getAccountData(uuid).getVirtualCurrencies();
    }

    // SENSITIVE ! DO NOT USE THIS METHOD TO SET BALANCE IF YOU DON'T KNOW WHAT YOU'RE DOING
    public void setRawBalance(UUID uuid, BigDecimal balance) {
        getCoinsData(uuid).setCurrentCoins(balance);
    }
}
