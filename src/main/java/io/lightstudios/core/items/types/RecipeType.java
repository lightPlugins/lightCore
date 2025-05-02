package io.lightstudios.core.items.types;

import javax.annotation.Nullable;

public enum RecipeType {

    WORKBENCH,
    PLAYER;

    @Nullable
    public static RecipeType valueOfNullable(String name) {
        if (name == null) {
            return null;
        }
        for (RecipeType type : values()) {
            if (type.name().equalsIgnoreCase(name.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
}
