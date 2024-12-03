package io.lightstudios.core.inventory.handler;

import io.lightstudios.core.inventory.actions.MessageAction;
import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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