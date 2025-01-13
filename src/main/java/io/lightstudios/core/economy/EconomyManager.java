package io.lightstudios.core.economy;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.hooks.lightcoins.LightCoinsManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.List;

public class EconomyManager {

    private Economy vault = null;
    private LightCoinsManager lightCoinsManager = null;
    private boolean useLightCoins = false;

    public EconomyManager() {
        if(!setupVault()) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not find an economy plugin!",
                    "Please install an economy plugin like LightCoins!"
            ));

            if(setupLightCoins()) {
                LightCore.instance.getConsolePrinter().printInfo("Found §eLightCoins §ras economy plugin.");
                this.useLightCoins = true;
            }
        }
    }

    public BigDecimal getBalance(OfflinePlayer offlinePlayer) {
        if(useLightCoins) {
            return lightCoinsManager.getBalance(offlinePlayer.getUniqueId());
        } else {
            return BigDecimal.valueOf(vault.getBalance(offlinePlayer));
        }
    }

    public boolean hasEnough(OfflinePlayer offlinePlayer, BigDecimal amount) {
        if(useLightCoins) {
            return lightCoinsManager.hasEnough(offlinePlayer.getUniqueId(), amount);
        } else {
            return vault.has(offlinePlayer, amount.doubleValue());
        }
    }

    public EconomyResponse deposit(OfflinePlayer offlinePlayer, BigDecimal amount) {
        if(useLightCoins) {
            return lightCoinsManager.deposit(offlinePlayer.getUniqueId(), amount);
        } else {
            return vault.depositPlayer(offlinePlayer, amount.doubleValue());
        }
    }

    public EconomyResponse withdraw(OfflinePlayer offlinePlayer, BigDecimal amount) {
        if(useLightCoins) {
            return lightCoinsManager.withdraw(offlinePlayer.getUniqueId(), amount);
        } else {
            return vault.withdrawPlayer(offlinePlayer, amount.doubleValue());
        }
    }

    private boolean setupVault() {
        RegisteredServiceProvider<Economy> rsp = LightCore.instance.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vault = rsp.getProvider();
        return true;
    }

    private boolean setupLightCoins() {
        if(LightCore.instance.getHookManager().isExistLightCoins()) {
            this.lightCoinsManager = LightCore.instance.getHookManager().getLightCoinsManager();
            return true;
        }
        return false;
    }




}
