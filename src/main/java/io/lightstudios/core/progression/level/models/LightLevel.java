package io.lightstudios.core.progression.level.models;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class LightLevel {

    private Map<Integer, SingleLevel> levels;
    private Component name;

    @Getter
    @Setter
    public static class SingleLevel {

        private int level;
        private BigDecimal requiredXP;

    }

}
