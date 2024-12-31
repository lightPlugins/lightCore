package io.lightstudios.core.hooks;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.hooks.lightcoins.LightCoinsManager;
import io.lightstudios.core.hooks.placeholderapi.PlaceholderAPIManager;
import io.lightstudios.core.hooks.protocollib.ProtocolLibManager;
import io.lightstudios.core.hooks.towny.TownyInterface;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class HookManager {

    private boolean existTowny = false;
    private boolean existProtocolLib = false;
    private boolean existPlaceholderAPI = false;
    private boolean existLightCoins = false;

    private TownyInterface townyInterface;
    private ProtocolLibManager protocolLibManager;
    private PlaceholderAPIManager placeholderAPIManager;
    private LightCoinsManager lightCoinsManager;

    public HookManager () {
        // check if LightCoins is installed
        checkLightCoins();
        // check if Towny is installed
        checkTowny();
        // check if ProtocolLib is installed
        checkProtocolLib();
        // check if PlaceholderAPI is installed
        checkPlaceholderAPI();
    }


    private void checkLightCoins() {
        if(Bukkit.getServer().getPluginManager().getPlugin("LightCoins") != null) {
            this.existLightCoins = true;
            this.lightCoinsManager = new LightCoinsManager();
            LightCore.instance.getConsolePrinter().printInfo("Found plugin LightCoins. Hooking into it.");
        }
    }

    private void checkTowny() {

        if(Bukkit.getServer().getPluginManager().getPlugin("Towny") != null) {
            this.existTowny = true;
            this.townyInterface = new TownyInterface();
            LightCore.instance.getConsolePrinter().printInfo("Found plugin Towny. Hooking into it.");
        }
    }

    private void checkProtocolLib() {

        if(Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            this.existProtocolLib = true;
            this.protocolLibManager = new ProtocolLibManager();
            LightCore.instance.getConsolePrinter().printInfo("Found plugin ProtocolLib. Hooking into it.");
        }
    }

    private void checkPlaceholderAPI() {

        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.existPlaceholderAPI = true;
            this.placeholderAPIManager = new PlaceholderAPIManager();
            LightCore.instance.getConsolePrinter().printInfo("Found plugin PlaceholderAPI. Hooking into it.");
        }
    }

}
