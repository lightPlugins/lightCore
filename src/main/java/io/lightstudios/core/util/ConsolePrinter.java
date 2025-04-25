package io.lightstudios.core.util;

import org.bukkit.Bukkit;

import java.util.List;

public class ConsolePrinter {

    private final boolean isDebugEnabled = true;
    private final int debugLevel = 1;
    private final String prefix;

    public ConsolePrinter(String prefix) {
        this.prefix = prefix;
    }

    public void printInfo(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§rINFO§8] §r" + message);
    }

    public void printInfo(List<String> messages) {
        sendEmptyLine();
        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§8[§rINFO§8] §r" + message);
        }
        sendEmptyLine();
    }

    public void printWarning(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§6WARNING§8] §e" + message);
    }

    public void printWarning(List<String> messages) {
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("     §6# §e############## §6WARNING §e##############  §6#");
        Bukkit.getConsoleSender().sendMessage(" ");
        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§8[§6WARNING§8] §e" + message);
        }
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("     §6# §e###################################  §6#");
        Bukkit.getConsoleSender().sendMessage(" ");
    }

    public void printError(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4ERROR§8] §c" + message);
    }

    public void printError(List<String> messages) {
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("     §4# §c############## §4ERROR §c##############  §4#");
        Bukkit.getConsoleSender().sendMessage(" ");
        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4ERROR§8] §c" + message);
        }
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("     §4# §c###################################  §4#");
        Bukkit.getConsoleSender().sendMessage(" ");
    }

    public void printConfigError(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4CONFIG-ERROR§8] §c" + message);
    }

    public void printConfigError(List<String> messages) {
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("     §4# §c########### §4CONFIG-ERROR §c###########  §4#");
        Bukkit.getConsoleSender().sendMessage(" ");
        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4CONFIG-ERROR§8] §c" + message);
        }
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("     §4# §c###################################  §4#");
        Bukkit.getConsoleSender().sendMessage(" ");
    }

    public void printItemSystem(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§rITEMS§8] §r" + message);
    }

    public void printItemSystem(List<String> messages) {
        sendEmptyLine();
        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§8[§rITEMS§8] §r" + message);
        }
        sendEmptyLine();
    }

    public void printDebug(List<String> messages, int debugLevel) {
        switch (debugLevel) {
            case 1:
                if (isDebugEnabled && this.debugLevel >= 1) {
                    for (String message : messages) {
                        printDebug(prefix + "§8[§3DEBUG§8] §b" + message);
                    }
                }
                break;
            case 2:
                if (isDebugEnabled && this.debugLevel >= 2) {
                    for (String message : messages) {
                        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§3DEBUG§8] §b" + message);
                    }
                }
                break;
            case 3:
                if (isDebugEnabled && this.debugLevel >= 3) {
                    for (String message : messages) {
                        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§3DEBUG§8] §b" + message);
                    }
                }
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(prefix + "\n§8[§4ERROR§8] §c" + "Invalid debug level");
                Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4ERROR§8] §c" + "Check the core.yml and make sure the debug level is between 1 and 3\n");
                break;
        }
    }

    public void printDebug(String message, int debugLevel) {

        switch (debugLevel) {
            case 1:
                if (isDebugEnabled && this.debugLevel >= 1) {
                    printDebug(prefix + "§8[§3DEBUG§8] §b" + message);
                }
                break;
            case 2:
                if (isDebugEnabled && this.debugLevel >= 2) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§8[§3DEBUG§8] §b" + message);
                }
                break;
            case 3:
                if (isDebugEnabled && this.debugLevel >= 3) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§8[§3DEBUG§8] §b" + message);
                }
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(prefix + "\n§8[§4ERROR§8] §c" + "Invalid debug level");
                Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4ERROR§8] §c" + "Check the core.yml and make sure the debug level is between 1 and 3\n");
                break;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + "§8[§4ERROR§8] §c" + message);
    }

    public void printDebug(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    private void sendEmptyLine() {
        String newLine = " ";
        Bukkit.getConsoleSender().sendMessage(newLine);
    }

}
