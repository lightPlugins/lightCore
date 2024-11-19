package io.lightstudios.core.register;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.interfaces.LightModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegister {

    CommandRegister commandRegister = new CommandRegister(null, new ArrayList<>());

    public void registerModules(List<LightModule> modules) {
        for (LightModule module : modules) {
            if(!module.isEnabled()) {
                module.enable();
                LightCore.instance.getConsolePrinter().printInfo("Module " + module.getName() + " has been enabled.");
            }
        }
    }

    public void unregisterModules(List<LightModule> modules) {
        for (LightModule module : modules) {
            if(module.isEnabled()) {
                module.disable();
                LightCore.instance.getConsolePrinter().printInfo("Module " + module.getName() + " has been disabled.");
            }
        }
    }
}
