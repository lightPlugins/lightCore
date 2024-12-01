package io.lightstudios.core.exceptions;

public class DuplicateItemName extends Exception {

    public DuplicateItemName(String itemName) {
        super("The item name §e" + itemName + "§r is already in use. Please choose another name");
    }



}
