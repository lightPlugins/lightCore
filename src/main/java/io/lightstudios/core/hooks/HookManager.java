package io.lightstudios.core.hooks;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.hooks.protocollib.ProtocolLibManager;
import io.lightstudios.core.hooks.towny.TownyInterface;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class HookManager {

    private boolean existTowny = false;
    private boolean existProtocolLib = false;
    private TownyInterface townyInterface;
    private ProtocolLibManager protocolLibManager;

    public HookManager () {
        // check if Towny is installed
        checkTowny();
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

}
