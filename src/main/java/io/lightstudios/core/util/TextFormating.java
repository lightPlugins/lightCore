package io.lightstudios.core.util;

public class TextFormating {

    public String formatName(String input) {
        // Text in Kleinbuchstaben und in Wörter splitten
        String[] words = input.toLowerCase().split("_");

        // Jedes Wort formatieren: erstes Zeichen groß, Rest klein
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            formattedName.append(Character.toUpperCase(word.charAt(0))) // Ersten Buchstaben groß
                    .append(word.substring(1))                    // Rest klein
                    .append(" ");                                 // Leerzeichen hinzufügen
        }

        // Letztes Leerzeichen entfernen und String zurückgeben
        return formattedName.toString().trim();
    }
}
