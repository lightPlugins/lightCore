package io.lightstudios.core.commands.manager;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.CompositeTabCompleter;
import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandManager implements CommandExecutor {

    private final ArrayList<LightCommand> subCommands;
    private ArrayList<LightCommand> getSubCommands() {
        return subCommands;
    }

    private void registerCommand(PluginCommand command) {
        if (command != null) {
            command.setExecutor(this);
            Map<String, TabCompleter> subCommandTabCompleter = new HashMap<>();
            List<String> ecoSubCommands = new ArrayList<>(); // Liste der Subcommands von /eco

            LightCore.instance.getConsolePrinter().printInfo(
                    "Successfully registered command " + command.getName());

            for (LightCommand subCommand : getSubCommands()) {
                TabCompleter tabCompleter = subCommand.registerTabCompleter();
                if (tabCompleter != null) {
                    List<String> subCommandNames = subCommand.getSubcommand();
                    for (String subCommandName : subCommandNames) {
                        subCommandTabCompleter.put(subCommandName, tabCompleter);
                        ecoSubCommands.add(subCommandName); // Füge den Subcommand-Namen zur Liste hinzu
                        LightCore.instance.getConsolePrinter().printInfo(
                                "Successfully registered tab completer for " + subCommandName);
                    }
                }
            }

            if (!subCommandTabCompleter.isEmpty()) {
                command.setTabCompleter(new CompositeTabCompleter(subCommandTabCompleter, ecoSubCommands));
            }
        }
    }

    public CommandManager(PluginCommand command, ArrayList<LightCommand> subCommands) {
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
                            if(player.hasPermission(subCommand.getPermission())) {
                                if(args.length != subCommand.maxArgs()) {
                                    LightCore.instance.getMessageSender().sendPlayerMessage(
                                            player, LightCore.instance.getMessages().wrongSyntax()
                                                    .replace("#syntax#", subCommand.getSyntax()));
                                    return false;
                                }
                                subCommand.performAsPlayer(player, args);
                                return true;
                            } else {
                                LightCore.instance.getMessageSender().sendPlayerMessage(
                                        player, LightCore.instance.getMessages().noPermission()
                                                .replace("#permission#", subCommand.getPermission()));
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

            if(command.getName().equals("lightcore")) {
                // stuff
            }

        return false;
    }
}
