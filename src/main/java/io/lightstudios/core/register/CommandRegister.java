package io.lightstudios.core.register;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.CompositeTabCompleter;
import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandRegister implements CommandExecutor {

    private final ArrayList<LightCommand> subCommands;

    private ArrayList<LightCommand> getSubCommands() {
        return subCommands;
    }

    private void registerCommand(PluginCommand command) {
        if (command != null) {
            command.setExecutor(this);
            Map<String, TabCompleter> subCommandTabCompletes = new HashMap<>();
            List<String> ecoSubCommands = new ArrayList<>(); // List of subcommands for /eco

            for (LightCommand subCommand : getSubCommands()) {
                LightCore.instance.getConsolePrinter().printInfo(
                        "Register subcommand: " + subCommand.getSubcommand() + " for root command: " + command.getName());
                TabCompleter tabCompleter = subCommand.registerTabCompleter();
                if (tabCompleter != null) {
                    List<String> subCommandNames = subCommand.getSubcommand();
                    for (String subCommandName : subCommandNames) {
                        subCommandTabCompletes.put(subCommandName, tabCompleter);
                        ecoSubCommands.add(subCommandName); // Add subcommand name to the list
                    }
                }
            }

            if (!subCommandTabCompletes.isEmpty()) {
                command.setTabCompleter(new CompositeTabCompleter(subCommandTabCompletes, ecoSubCommands));
            }
        }
    }

    public CommandRegister(PluginCommand command, ArrayList<LightCommand> subCommands) {
        this.subCommands = subCommands;
        registerCommand(command);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (args.length > 0) {
            for (LightCommand subCommand : getSubCommands()) {
                if (subCommand.getSubcommand().contains(args[0])) {

                    if (sender instanceof Player player) {
                        if (player.hasPermission(subCommand.getPermission())) {
                            if (args.length != subCommand.maxArgs()) {
                                LightCore.instance.getMessageSender().sendPlayerMessage(player, "§cSyntax: §7" + subCommand.getSyntax());
                                return false;
                            }
                            subCommand.performAsPlayer(player, args);
                            return true;
                        } else {
                            LightCore.instance.getMessageSender().sendPlayerMessage(player, "§cSyntax: §7" + subCommand.getPermission());
                            return false;
                        }
                    }

                    if (sender instanceof ConsoleCommandSender console) {
                        subCommand.performAsConsole(console, args);
                        return true;
                    }
                }
            }
        }

        if (command.getName().equals("eco")) {
            // TODO: Implement raw command without subcommands (if needed)
        }

        return false;
    }
}
