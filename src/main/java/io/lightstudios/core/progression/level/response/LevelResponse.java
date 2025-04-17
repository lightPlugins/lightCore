package io.lightstudios.core.progression.level.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LevelResponse {

    public enum Status {
        SUCCESS,
        FAILURE
    }

    private final Status status;
    private final String message;

}
