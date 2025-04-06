package me.ikevoodoo.devroomtrial.listeners;

import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import me.ikevoodoo.devroomtrial.particles.ParticleRendererManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerWandListener implements Listener {

    private final ParticleRendererManager particleManager;

    public PlayerWandListener(ParticleRendererManager particleManager) {
        this.particleManager = particleManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        final var player = event.getPlayer();
        final var item = player.getInventory().getItem(EquipmentSlot.HAND);
        if (item == null) {
            return;
        }

        final var selectionManager = SelectionManager.instance();

        if (!selectionManager.isWand(item)) {
            return;
        }

        event.setCancelled(true);

        final var block = event.getBlock();
        final var position = block.getLocation().toVector();

        selectionManager.updateSelectionStart(player, position);

        this.particleManager.startRenderer(player);

        player.sendMessage("§7Successfully updated first position to (§c%d§7, §a%d§7, §3%d§7)".formatted(
                position.getBlockX(),
                position.getBlockY(),
                position.getBlockZ()
        ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        final var stack = event.getItem();
        if (stack == null) {
            return;
        }

        final var selectionManager = SelectionManager.instance();

        if (!selectionManager.isWand(stack)) {
            return;
        }

        event.setCancelled(true);

        final var player = event.getPlayer();
        final var block = event.getClickedBlock();
        final var position = block == null
                ? player.getLocation().toVector()
                : block.getLocation().toVector();

        selectionManager.updateSelectionEnd(player, position);

        this.particleManager.startRenderer(player);

        player.sendMessage("§7Successfully updated last position to (§c%d§7, §a%d§7, §3%d§7)".formatted(
                position.getBlockX(),
                position.getBlockY(),
                position.getBlockZ()
        ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Restart the task when a player logs back in
        this.particleManager.startRenderer(event.getPlayer());
    }


}
