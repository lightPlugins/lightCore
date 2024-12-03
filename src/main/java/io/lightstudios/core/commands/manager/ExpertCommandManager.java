package io.lightstudios.core.commands.manager;

import io.lightstudios.core.LightCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class ExpertCommandManager {

    private final CommandMap commandMap;

    public ExpertCommandManager() {
        this.commandMap = getCommandMap();
    }

    private CommandMap getCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get CommandMap in ExpertCommandManager", e);
        }
    }

    public void registerCommand(String name, String description, List<String> aliases, Plugin plugin, CommandExecutor executor, TabCompleter tabCompleter) {
        CustomCommand command = new CustomCommand(name, description, "/" + name, aliases, executor, tabCompleter);
        commandMap.register(plugin.getDescription().getName(), command);
    }

    public interface CommandExecutor {
        boolean onCommand(CommandSender sender, Command command, String label, String[] args);
    }

    private static class CustomCommand extends Command implements TabCompleter {
        private final CommandExecutor executor;
        private final TabCompleter tabCompleter;

        public CustomCommand(String name, String description, String usageMessage, List<String> aliases, CommandExecutor executor, TabCompleter tabCompleter) {
            super(name, description, usageMessage, aliases);
            this.executor = executor;
            this.tabCompleter = tabCompleter;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
            return executor.onCommand(sender, this, commandLabel, args);
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
            LightCore.instance.getConsolePrinter().printInfo("onTabComplete aufgerufen mit args: " + String.join(" ", args));
            return tabCompleter.onTabComplete(sender, command, alias, args);
        }
    }
}