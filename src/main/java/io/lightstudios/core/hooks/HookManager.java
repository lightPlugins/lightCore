package io.lightstudios.core.hooks;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.hooks.fancyholograms.HologramManager;
import io.lightstudios.core.hooks.lightcoins.LightCoinsManager;
import io.lightstudios.core.hooks.nexo.NexoManager;
import io.lightstudios.core.hooks.placeholderapi.PlaceholderAPIManager;
import io.lightstudios.core.hooks.towny.TownyInterface;
import io.lightstudios.core.world.WorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Getter
public class HookManager {

    private boolean existTowny = false;
    private boolean existPlaceholderAPI = false;
    private boolean existLightCoins = false;
    private boolean existNexo = false;
    private boolean existFancyHolograms = false;
    private boolean existWorldGuard = false;

    private TownyInterface townyInterface;
    private PlaceholderAPIManager placeholderAPIManager;
    private LightCoinsManager lightCoinsManager;
    private NexoManager nexoManager;
    private HologramManager hologramManager;
    private WorldManager worldManager;

    public HookManager() {
        existLightCoins = checkPlugin("LightCoins", LightCoinsManager.class);
        existTowny = checkPlugin("Towny", TownyInterface.class);
        existPlaceholderAPI = checkPlugin("PlaceholderAPI", PlaceholderAPIManager.class);
        existNexo = checkPlugin("Nexo", NexoManager.class);
        existFancyHolograms = checkPlugin("FancyHolograms", HologramManager.class);
        existWorldGuard = checkPlugin("WorldGuard", WorldManager.class);
    }

    private <T> boolean checkPlugin(String pluginName, Class<T> managerClass) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            try {
                T manager = managerClass.getDeclaredConstructor().newInstance();
                setManager(managerClass, manager);
                LightCore.instance.getConsolePrinter().printInfo("Found plugin " + pluginName + ". Hooking into it.");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private <T> void setManager(Class<T> managerClass, T manager) {
        if (managerClass == LightCoinsManager.class) {
            lightCoinsManager = (LightCoinsManager) manager;
        } else if (managerClass == TownyInterface.class) {
            townyInterface = (TownyInterface) manager;
        } else if (managerClass == PlaceholderAPIManager.class) {
            placeholderAPIManager = (PlaceholderAPIManager) manager;
        } else if (managerClass == NexoManager.class) {
            nexoManager = (NexoManager) manager;
        } else if (managerClass == HologramManager.class) {
            hologramManager = (HologramManager) manager;
        } else {
            worldManager = (WorldManager) manager;
        }
    }

}
