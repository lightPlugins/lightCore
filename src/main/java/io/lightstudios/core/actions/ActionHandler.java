package io.lightstudios.core.actions;

import io.lightstudios.core.actions.types.*;
import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles actions for a player based on provided action data.
 * Author: lightPlugins
 * Copyright: © 2023 [lightStudios]. All rights reserved.
 * You may not use, distribute, or modify this code without explicit permission.
 */

public class ActionHandler {

    private static final Map<String, LightAction> actions = new HashMap<>();

    static {
        initializeActions();
    }

    private final Player player;
    private final String[] actionDataArray;

    public ActionHandler(Player player, String actionData) {
        this.player = player;
        this.actionDataArray = actionData.split(";");
    }

    private static void initializeActions() {
        actions.put("send-message", new MessageAction());
        actions.put("player-command", new PlayerCmdAction());
        actions.put("console-command", new ConsoleCmdAction());
        actions.put("give-item", new GiveItemAction());
        actions.put("inventory-close", new InvCloseAction());
        actions.put("title", new TitleAction());
        actions.put("actionbar", new ActionBarAction());
        actions.put("bossbar", new BossBarAction());
        actions.put("play-sound", new PlaySoundAction());
    }

    public String[] getActions() {
        return actionDataArray;
    }

    public void handleAction() {
        if (actionDataArray == null) {
            return;
        }

        LightAction lightAction = actions.get(actionDataArray[0]);

        if (lightAction != null) {
            lightAction.execute(player, actionDataArray);
        }
    }
}