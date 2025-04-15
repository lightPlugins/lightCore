package io.lightstudios.core.progression.level.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class LightLevelData {

    private UUID uuid;
    private LightLevel level;
    private BigDecimal currentXP;
    private int currentLevel;

}
