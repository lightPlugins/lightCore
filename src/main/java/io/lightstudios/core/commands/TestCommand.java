package io.lightstudios.core.commands;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand implements LightCommand {
    @Override
    public List<String> getSubcommand() {
        return List.of("subcommand");
    }

    @Override
    public String getDescription() {
        return "just a test command";
    }

    @Override
    public String getSyntax() {
        return "/peter subcommand";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "test.permission";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (sender, command, alias, args) ->  {
            if(args.length == 1) {
                return List.of("subcommand");
            }
            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) {
        LightCore.instance.getMessageSender().sendPlayerMessage(player, "Test command executed");
        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) {
        LightCore.instance.getConsolePrinter().printInfo("Test command executed");
        return false;
    }
}
