package io.lightstudios.core.inventory.constructor;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvConstructor {

    public String guiName;
    public String guiTitle;
    public int rows;
    public List<String> pattern = new ArrayList<>();
    public ConfigurationSection clickItemHandlersSection;
    public List<String> actionHandlersList = new ArrayList<>();

}
