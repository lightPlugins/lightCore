package io.lightstudios.core.commands;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.items.LightItem;
import io.lightstudios.core.util.interfaces.LightCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class LightItemsCommand implements LightCommand {
    @Override
    public List<String> getSubcommand() {
        return List.of("lightitem");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/core <item-id>";
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
            if (args.length == 2) {
                return LightCore.instance.getItemManager().getItems().keySet().stream()
                        .map(lightItemID -> lightItemID.split(":")[1]).toList();
            }
            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) {

        LightItem lightItem = LightCore.instance.getItemManager().getItemByName("lightcore:" + args[1]);

        if(lightItem != null) {
            player.getInventory().addItem(lightItem.getItemStack());
            return true;
        }

        player.sendMessage(Component.text("§clightItem is null for id: " + args[1]));

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) {
        return false;
    }
}
