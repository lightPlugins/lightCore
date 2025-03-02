package io.lightstudios.core.commands.tabcomplete;

import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompositeTabCompleter implements TabCompleter {
    private final Map<String, TabCompleter> subCommandTabCompleter;
    private final List<LightCommand> subCommands; // List of LightCommands

    public CompositeTabCompleter(Map<String, TabCompleter> subCommandTabCompleters, List<LightCommand> subCommands) {
        this.subCommandTabCompleter = subCommandTabCompleters;
        this.subCommands = subCommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player player) {
                return subCommands.stream()
                        .filter(lightCommand -> player.hasPermission(lightCommand.getPermission()) || lightCommand.getPermission().isEmpty())
                        .map(LightCommand::getSubcommand)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
            return subCommands.stream()
                    .map(LightCommand::getSubcommand)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            TabCompleter tabCompleter = subCommandTabCompleter.get(args[0]);
            if (tabCompleter != null) {
                return tabCompleter.onTabComplete(sender, command, alias, args);
            }
        }
        return Collections.emptyList();
    }
}