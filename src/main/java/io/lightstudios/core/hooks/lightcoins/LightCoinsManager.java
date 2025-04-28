package io.lightstudios.core.hooks.lightcoins;

import io.lightstudios.coins.LightCoins;
import io.lightstudios.coins.api.LightCoinsAPI;
import io.lightstudios.coins.api.models.AccountData;
import io.lightstudios.coins.api.models.CoinsData;
import io.lightstudios.coins.api.models.VirtualData;
import io.lightstudios.coins.api.types.EconomyReason;
import io.lightstudios.core.LightCore;
import net.milkbowl.vault.economy.EconomyResponse;

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
    // USE deposit, withdraw, setBalance INSTEAD
    public void setRawBalance(UUID uuid, BigDecimal balance) {
        getCoinsData(uuid).setCurrentCoins(balance);
    }

    public BigDecimal getBalance(UUID uuid) {
        return getCoinsData(uuid).getCurrentCoins();
    }

    public boolean hasEnough(UUID uuid, BigDecimal amount) {
        return getCoinsData(uuid).hasEnough(amount);
    }

    public EconomyResponse deposit(UUID uuid, BigDecimal amount) {
        return getCoinsData(uuid).addCoins(amount, EconomyReason.CORE);
    }

    public EconomyResponse withdraw(UUID uuid, BigDecimal amount) {
        return getCoinsData(uuid).removeCoins(amount, EconomyReason.CORE);
    }

    public EconomyResponse setBalance(UUID uuid, BigDecimal amount) {
        return getCoinsData(uuid).setCoins(amount, EconomyReason.CORE);
    }

}
