package me.ikevoodoo.devroomtrial.flags;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Objects;

public class BlockPlaceFlag extends Flag implements Listener {

    public BlockPlaceFlag(String id, Region region) {
        super(id, region);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.state() == FlagState.EVERYONE) {
            return;
        }

        final var player = event.getPlayer();
        final var block = event.getBlock();

        final var location = block.getLocation();
        final var worldId = Objects.requireNonNull(location.getWorld()).getUID();

        if (!this.region().worldId().equals(worldId)) {
            return;
        }

        if (!this.region().boundingBox().contains(location.toVector())) {
            return;
        }

        if (this.state() == FlagState.NONE) {
            event.setCancelled(true);
            player.sendMessage("§cBlocks can't be placed in this region!");
            return;
        }

        if (!this.region().whitelist().contains(player)) {
            event.setCancelled(true);
            player.sendMessage("§cOnly whitelisted players can place in this region!");
        }
    }


}
