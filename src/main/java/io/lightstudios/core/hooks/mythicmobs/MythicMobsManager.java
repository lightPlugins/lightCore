package io.lightstudios.core.hooks.mythicmobs;

import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.bukkit.MythicBukkit;


public class MythicMobsManager {

    public MythicBukkit getMythicBukkit() {
        return MythicBukkit.inst();
    }

    public MobManager getMobManager() {
        return getMythicBukkit().getMobManager();
    }
}
