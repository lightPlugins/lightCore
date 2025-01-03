package io.lightstudios.core.commands;

import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class PushSQLiteToMySQL implements LightCommand {


    @Override
    public List<String> getSubcommand() {
        return List.of("pushTo");
    }

    @Override
    public String getDescription() {
        return "Pushes ALL SQLite data to MySQL";
    }

    @Override
    public String getSyntax() {
        return "/lightcore pushTo mysql";
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "lightcore.command.admin.pushto";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (sender, command, alias, args) -> {
            if(args.length == 1) {
                return getSubcommand();
            }

            if(args.length == 2) {
                return List.of("mysql");
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) {
        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) {
        return false;
    }
}
