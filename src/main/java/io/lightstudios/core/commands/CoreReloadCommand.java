package io.lightstudios.core.commands;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.items.LightItem;
import io.lightstudios.core.util.files.FileManager;
import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class CoreReloadCommand implements LightCommand {
    @Override
    public List<String> getSubcommand() {
        return List.of("reload");
    }

    @Override
    public String getDescription() {
        return "Reloads the core plugin";
    }

    @Override
    public String getSyntax() {
        return "/lightcore reload";
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "lightcore.command.admin.reload";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (sender, command, alias, args) -> {

            if(args.length == 1) {
                return getSubcommand();
            }

            if(args.length == 2) {
                return List.of("lobby", "farmwelt");
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) {
        LightCore.instance.reloadCore();

        HashMap<String, LightItem> updatedItems =
                LightCore.instance.getItemManager().updateItems(LightCore.instance, LightCore.instance.getItemFiles().getYamlFiles());

        LightCore.instance.getConsolePrinter().printInfo("Updated " + updatedItems.size() + " items");

        LightCore.instance.getMessageSender().sendPlayerMessage(player, LightCore.instance.getMessages().coreReload());

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) {
        LightCore.instance.reloadCore();
        return false;
    }
}
