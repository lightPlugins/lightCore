package io.lightstudios.core.hooks.mythicmobs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

public class MythicMobsManager {

    public MythicBukkit getMythicBukkit() {
        return MythicBukkit.inst();
    }
    public MobExecutor getMobManager() {
        return getMythicBukkit().getMobManager();
    }


    public boolean isMythicMobByEntity(LivingEntity entity) {
        return getMobManager().isMythicMob(entity);
    }
    public boolean isMythicMobByEntity(Entity entity) {
        return getMobManager().isMythicMob(entity);
    }


    public ActiveMob getActiveMobByEntity(Entity entity) {
        Optional<ActiveMob> optActiveMob = getMythicBukkit().getMobManager().getActiveMob(entity.getUniqueId());
        return optActiveMob.orElse(null);
    }
    public ActiveMob getActiveMobByEntity(LivingEntity entity) {
        Optional<ActiveMob> optActiveMob = getMythicBukkit().getMobManager().getActiveMob(entity.getUniqueId());
        return optActiveMob.orElse(null);
    }

    public LivingEntity spawnMythicMob(String mobName, Location location) {
        ActiveMob mob = getMobManager().spawnMob(mobName, location);

        if(mob != null) {
            return (LivingEntity) mob.getEntity().getBukkitEntity();
        } else {
            // mobName is not a valid mythic mob ID
            return null;
        }
    }

    public LivingEntity spawnMythicMob(String mobName, Location location, double level) {
        ActiveMob mob = getMobManager().spawnMob(mobName, location, level);

        if(mob != null) {
            return (LivingEntity) mob.getEntity().getBukkitEntity();
        } else {
            // mobName is not a valid mythic mob ID
            return null;
        }
    }

}
