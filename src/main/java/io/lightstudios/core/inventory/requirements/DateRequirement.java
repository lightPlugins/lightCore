package io.lightstudios.core.inventory.requirements;

import io.lightstudios.core.util.interfaces.LightRequirement;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRequirement implements LightRequirement {

    @Override
    public boolean checkRequirement(Player player, String[] requirementDataArray) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = simpleDateFormat.parse(requirementDataArray[1]);
            return !date.after(new Date());

        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing the date for requirement: " + requirementDataArray[1], e);
        }
    }
}
