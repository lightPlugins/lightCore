package io.lightstudios.core.commands;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.messaging.backend.send.SendTeleportRequest;
import io.lightstudios.core.util.interfaces.LightCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        return "";
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
        LightCore.instance.getMessageSender().sendPlayerMessage(player, LightCore.instance.getMessages().coreReload());
            //  70.5 76.0 527.5 10 15
        SendTeleportRequest.sendTeleportRequest(player, args[1], "world", 70.5, 76, 527.5, 10, 15);

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) {
        LightCore.instance.reloadCore();
        return false;
    }
}
