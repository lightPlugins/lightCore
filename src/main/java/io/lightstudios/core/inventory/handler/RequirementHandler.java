package io.lightstudios.core.inventory.handler;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.inventory.requirements.DateRequirement;
import io.lightstudios.core.util.interfaces.LightRequirement;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RequirementHandler {

    private static final Map<String, LightRequirement> requirements = new HashMap<>();

    static {
        initializeRequirements();
    }

    private final Player player;
    private final String[] requirementDataArray;

    public RequirementHandler(Player player, String requirementData) {
        this.player = player;
        this.requirementDataArray = requirementData.split(";");
    }

    public static void initializeRequirements() {
        requirements.put("date", new DateRequirement());
    }

    /**
     * Check if the requirements are met.
     * @return True if the requirements are met, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkRequirements() {
        if (requirementDataArray == null) {
            LightCore.instance.getConsolePrinter().printError("RequirementHandler -> RequirementDataArray is null !!!");
            return false;
        }

        LightRequirement lightRequirement = requirements.get(requirementDataArray[0]);

        if (lightRequirement != null) {
            return lightRequirement.checkRequirement(player, requirementDataArray);
        }

        LightCore.instance.getConsolePrinter().printError("Something went wrong with the requirements.");
        LightCore.instance.getConsolePrinter().printError("-> RequirementHandler -> LightRequirement is null");
        LightCore.instance.getConsolePrinter().printError("Activate fallback system. All requirements are now FALSE.");
        return false;
    }

    /**
     * Get the date requirement from the requirement data array if the requirement is a date.
     * @return The date requirement or null if the requirement is not a date.
     */
    public Date getDateRequirement() {
        if (requirementDataArray == null || !requirementDataArray[0].equals("date")) {
            return null;
        }

        String dateString = requirementDataArray[1];
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            LightCore.instance.getConsolePrinter().printError("Invalid date format in the config: " + dateString);
            return null;
        }
    }


}
