package me.ikevoodoo.devroomtrial.listeners;

import me.ikevoodoo.devroomtrial.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMenuListener implements Listener {

    private final Menu menu;

    public PlayerMenuListener(Menu menu) {
        this.menu = menu;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final var open = this.menu.getPage(player);
        if (open == null) {
            return;
        }

        event.setCancelled(true);

        final var stack = event.getCurrentItem();
        if (stack == null) {
            return;
        }

        if (!this.menu.isPaginator(stack)) {
            open.getRenderer().onClick(this.menu, player, event.getInventory(), open.getPageId(), event.getSlot());
            return;
        }

        final var leftClick = event.getClick().isLeftClick();

        final var opened = this.menu.openFromPaginator(player, stack, leftClick);
        if (!opened) {
            player.sendMessage("§cSorry! The requested page was not found.");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (this.menu.isTransitioning(player)) {
            return;
        }

        this.menu.close(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.menu.closeAll(event.getPlayer());
    }


}
