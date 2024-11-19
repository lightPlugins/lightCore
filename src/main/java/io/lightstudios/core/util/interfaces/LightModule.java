package io.lightstudios.core.util.interfaces;

import java.util.List;

public interface LightModule {

    void enable();
    void disable();
    void reload();
    boolean isEnabled();
    String getName();
    List<LightCommand> registerCommands();
}
