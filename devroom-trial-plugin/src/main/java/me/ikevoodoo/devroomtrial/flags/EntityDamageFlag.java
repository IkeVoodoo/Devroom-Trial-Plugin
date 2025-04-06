package me.ikevoodoo.devroomtrial.flags;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageFlag extends Flag implements Listener {

    public EntityDamageFlag(String id, Region region) {
        super(id, region);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.state() == FlagState.EVERYONE) {
            return;
        }

        final var entity = event.getEntity();

        final var worldId = entity.getWorld().getUID();

        if (!this.region().worldId().equals(worldId)) {
            return;
        }

        final var location = entity.getLocation().toVector();

        if (!this.region().boundingBox().contains(location)) {
            return;
        }

        if (this.state() == FlagState.NONE) {
            event.setCancelled(true);
            return;
        }

        if (!this.region().whitelist().contains(entity)) {
            event.setCancelled(true);
        }
    }


}
