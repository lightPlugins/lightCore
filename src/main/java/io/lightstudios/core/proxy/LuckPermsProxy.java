package io.lightstudios.core.proxy;

import io.lightstudios.core.LightCoreProxy;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/*
 *  ########## VELOCITY PROXY PLUGIN ##########
 *  WARNING: Do not use stuff from Bukkit here!
 */

public class LuckPermsProxy {

    @Nullable
    private LuckPerms luckPerms;

    public LuckPermsProxy() {
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            LightCoreProxy.instance.getConsolePrinter().sendError(List.of(
                    "LuckPerms is not loaded or not installed on this server.",
                    "Please install LuckPerms to use this feature."
            ));
        }
    }

    /**
     * Checks if the player is in the specified group.
     *
     * @param uuid The players uuid to check.
     * @param group  The group to check for.
     * @return True if the player is in the group, false otherwise. (including in error cases)
     */
    public CompletableFuture<Boolean> isProxyPlayerInGroup(UUID uuid, String group) {

        return CompletableFuture.supplyAsync(() -> {
            if (luckPerms == null) {
                return false;
            }

            UserManager userManager = luckPerms.getUserManager();
            User user = userManager.loadUser(uuid).join();

            if (user == null) {
                return false;
            }

            return user.getCachedData().getPermissionData().checkPermission("group." + group).asBoolean();
        }).exceptionally(e -> {
            LightCoreProxy.instance.getConsolePrinter().sendError(List.of(
                    "An error occurred while checking the group of player " + uuid,
                    "Error: " + e.getMessage()
            ));
            return false;
        });
    }

}
