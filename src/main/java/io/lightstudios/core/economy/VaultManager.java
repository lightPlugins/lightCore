package io.lightstudios.core.economy;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter
public class VaultManager {

    private final Economy economy;

    public VaultManager() {
        LightCore.instance.getConsolePrinter().printInfo("Hooking into Vault...");
        this.economy = setupEconomy();
    }

    private Economy setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if(rsp == null) {
            LightCore.instance.getConsolePrinter().printError("No economy plugin found! Disabling core plugin...");
            Bukkit.getPluginManager().disablePlugin(LightCore.instance);
            return null;
        }

        LightCore.instance.getConsolePrinter().printInfo("Hooked into " + rsp.getProvider().getName() + " economy plugin.");
        return rsp.getProvider();
    }

    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return this.economy.hasAccount(offlinePlayer);
    }

    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.economy.getBalance(offlinePlayer);
    }

    public EconomyResponse deposit(OfflinePlayer offlinePlayer, double amount) {
        return this.economy.depositPlayer(offlinePlayer, amount);
    }

    public EconomyResponse withdraw(OfflinePlayer offlinePlayer, double amount) {
        return this.economy.withdrawPlayer(offlinePlayer, amount);
    }

    public boolean hasEnough(OfflinePlayer offlinePlayer, double amount) {
        return this.economy.has(offlinePlayer, amount);
    }

    public boolean createAccount(OfflinePlayer offlinePlayer) {
        return this.economy.createPlayerAccount(offlinePlayer);
    }

}
