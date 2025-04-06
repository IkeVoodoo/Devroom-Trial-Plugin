package me.ikevoodoo.devroomtrial.flags;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;

public class BlockBreakFlag extends Flag implements Listener {

    public BlockBreakFlag(String id, Region region) {
        super(id, region);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
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
            player.sendMessage("§cBlocks can't be broken in this region!");
            return;
        }


        if (!this.region().whitelist().contains(player)) {
            event.setCancelled(true);
            player.sendMessage("§cOnly whitelisted players can break in this region!");
        }
    }


}
