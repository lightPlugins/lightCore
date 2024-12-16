package io.lightstudios.core.commands.manager;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.commands.tabcomplete.CompositeTabCompleter;
import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class CommandManager implements CommandExecutor {

    private final ArrayList<LightCommand> LightCommands;

    private ArrayList<LightCommand> getLightCommands() {
        return LightCommands;
    }

    public CommandManager(ArrayList<LightCommand> LightCommands, String commandName) {
        this.LightCommands = LightCommands;
        registerCommand(commandName);
    }

    public void registerCommand(String name) {
        PluginCommand command = createPluginCommand(name);
        if (command != null) {
            command.setExecutor(this);
            Map<String, TabCompleter> lightCommandTabCompleter = new HashMap<>();
            List<String> ecoLightCommands = new ArrayList<>(); // List of LightCommands for /eco

            LightCore.instance.getConsolePrinter().printInfo("Successfully registered command " + command.getName());

            for (LightCommand LightCommand : getLightCommands()) {
                TabCompleter tabCompleter = LightCommand.registerTabCompleter();
                if (tabCompleter != null) {
                    List<String> lightCommandNames = LightCommand.getSubcommand();

                    if(lightCommandNames.isEmpty()) {
                        command.setTabCompleter(tabCompleter);
                        continue;
                    }
                    for (String lightCommandName : lightCommandNames) {
                        lightCommandTabCompleter.put(lightCommandName, tabCompleter);
                        ecoLightCommands.add(lightCommandName);
                        LightCore.instance.getConsolePrinter().printDebug("Successfully registered tab completer for " + lightCommandName);
                    }
                }
            }

            if (!lightCommandTabCompleter.isEmpty()) {
                command.setTabCompleter(new CompositeTabCompleter(lightCommandTabCompleter, ecoLightCommands));
            }

            // Register the command with the server
            try {
                Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                // make the field accessible, because its private by default
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
                // use "" to register the command without the namespace
                // commandMap.register(name, command); /lightcore:lightcore reload
                // commandMap.register("", command); /lightcore reload
                commandMap.register("", command);
            } catch (Exception e) {
                throw new RuntimeException(new Exception("Could not register command " + name, e));
            }
        }
    }

    private PluginCommand createPluginCommand(String name) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            command = constructor.newInstance(name, LightCore.instance);
        } catch (Exception e) {
            throw new RuntimeException("Could not get declared constructor from Bukkit " + name, e);
        }
        return command;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        for (LightCommand lightCommand : getLightCommands()) {
            if (lightCommand.getSubcommand().isEmpty() || (args.length > 0 && lightCommand.getSubcommand().contains(args[0])) || (args.length == 0 && lightCommand.maxArgs() == -1)) {
                if (sender instanceof Player player) {

                    // check without permission if maxArgs is -1
                    if(lightCommand.maxArgs() == -1) {
                        lightCommand.performAsPlayer(player, args);
                        return true;
                    }

                    if (player.hasPermission(lightCommand.getPermission())) {

                        if (args.length != lightCommand.maxArgs() && !lightCommand.getSubcommand().isEmpty()) {
                            LightCore.instance.getMessageSender().sendPlayerMessage(player, LightCore.instance.getMessages().wrongSyntax()
                                    .replace("#syntax#", lightCommand.getSyntax()));
                            return false;
                        } else if(args.length > lightCommand.maxArgs() && !lightCommand.getSubcommand().isEmpty()) {
                            LightCore.instance.getMessageSender().sendPlayerMessage(player, LightCore.instance.getMessages().wrongSyntax()
                                    .replace("#syntax#", lightCommand.getSyntax()));
                            return false;
                        }
                        lightCommand.performAsPlayer(player, args);
                        return true;
                    } else {
                        LightCore.instance.getMessageSender().sendPlayerMessage(player, LightCore.instance.getMessages().noPermission()
                                .replace("#permission#", lightCommand.getSyntax()));
                        return false;
                    }
                }

                if (sender instanceof ConsoleCommandSender console) {
                    lightCommand.performAsConsole(console, args);
                    return true;
                }
            }
        }

        return false;
    }
}