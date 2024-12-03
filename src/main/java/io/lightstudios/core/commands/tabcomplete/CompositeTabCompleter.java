package io.lightstudios.core.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CompositeTabCompleter implements TabCompleter {
    private final Map<String, TabCompleter> subCommandTabCompleter;
    private final List<String> subCommands; // Liste der Subcommands von /eco

    public CompositeTabCompleter(Map<String, TabCompleter> subCommandTabCompleters, List<String> ecoSubCommands) {
        this.subCommandTabCompleter = subCommandTabCompleters;
        this.subCommands = ecoSubCommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands;
        } else if (args.length > 1) {
            TabCompleter tabCompleter = subCommandTabCompleter.get(args[0]);
            if (tabCompleter != null) {
                return tabCompleter.onTabComplete(sender, command, alias, args);
            }
        }
        return Collections.emptyList();
    }
}
