package io.lightstudios.core.items.types;

import javax.annotation.Nullable;

public enum ItemType {

    VANILLA,
    LIGHTCORE,
    MMOITEMS,
    NEXO,
    OTHER;

    @Nullable
    public static ItemType valueOfNullable(String name) {
        if (name == null) {
            return null;
        }
        for (ItemType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
