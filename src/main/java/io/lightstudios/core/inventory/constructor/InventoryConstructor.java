package io.lightstudios.core.inventory.constructor;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InventoryConstructor {

    private String guiName;
    private String guiTitle;
    private int rows;
    private List<String> pattern = new ArrayList<>();
    private ConfigurationSection clickItemHandlersSection;
    private List<String> actionHandlersList = new ArrayList<>();
    private int clickCooldownTime = 0;
    private int refreshRate = 20;

}
