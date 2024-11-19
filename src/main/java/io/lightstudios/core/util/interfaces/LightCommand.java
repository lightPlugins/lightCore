package io.lightstudios.core.util.interfaces;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public interface LightCommand {

    List<String> getSubcommand();
    String getDescription();
    String getSyntax();
    int maxArgs();
    String getPermission();
    TabCompleter registerTabCompleter();

    boolean performAsPlayer(Player player, String[] args);
    boolean performAsConsole(ConsoleCommandSender sender, String[] args);

}
