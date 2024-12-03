package io.lightstudios.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class SkullUtil {

    public static ItemStack getPlayerSkull(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if(meta == null) {
            return new ItemStack(Material.STONE, 1);
        }

        PlayerProfile profile = Bukkit.createPlayerProfile(player.getUniqueId(), player.getName());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(player.getPlayerProfile().getTextures().getSkin());
        profile.setTextures(textures);

        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }

    public static SkullMeta getSkullMeta(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if(meta == null) {
            throw new RuntimeException("SkullMeta is null");
        }

        PlayerProfile profile = Bukkit.createPlayerProfile(player.getUniqueId(), player.getName());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(player.getPlayerProfile().getTextures().getSkin());
        profile.setTextures(textures);

        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);

        return meta;
    }

    public static PlayerProfile getPlayerProfile(Player player) {
        PlayerProfile profile = Bukkit.createPlayerProfile(player.getUniqueId(), player.getName());
        PlayerTextures textures = profile.getTextures();
        profile.setTextures(textures);
        return profile;
    }
}
