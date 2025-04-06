package me.ikevoodoo.devroomtrial.flags;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractFlag extends Flag implements Listener {

    public InteractFlag(String id, Region region) {
        super(id, region);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.state() == FlagState.EVERYONE) {
            return;
        }

        final var player = event.getPlayer();

        final var worldId = player.getWorld().getUID();
        final var location = event.getClickedBlock() == null
                ? event.getClickedPosition()
                : event.getClickedBlock().getLocation().toVector();

        if (location == null) {
            return;
        }

        if (!this.region().worldId().equals(worldId)) {
            return;
        }

        if (!this.region().boundingBox().contains(location)) {
            return;
        }

        if (this.state() == FlagState.NONE) {
            event.setCancelled(true);
            player.sendMessage("§cYou can't interact in this region!");
            return;
        }

        if (!this.region().whitelist().contains(player)) {
            event.setCancelled(true);
            player.sendMessage("§cOnly whitelisted players can interact in this region!");
        }
    }


}
