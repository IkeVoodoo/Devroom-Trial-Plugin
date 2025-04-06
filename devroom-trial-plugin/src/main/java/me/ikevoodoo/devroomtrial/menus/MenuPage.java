package me.ikevoodoo.devroomtrial.menus;

import me.ikevoodoo.devroomtrial.menus.renderers.MenuRenderer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class MenuPage {

    private final String pageName;
    private final int pageId;
    private final MenuRenderer renderer;

    protected MenuPage(String pageName, int pageId, MenuRenderer renderer) {
        this.pageName = pageName;
        this.pageId = pageId;
        this.renderer = renderer;
    }

    public String getPageName() {
        return pageName;
    }

    public int getPageId() {
        return pageId;
    }

    public MenuRenderer getRenderer() {
        return renderer;
    }

    public InventoryView open(@NotNull final Menu menu, @NotNull final Player player) {
        final var inventory = this.renderer.createInventory(menu, player, this.pageId);

        this.renderer.render(menu, player, inventory, this.pageId);

        return player.openInventory(inventory);
    }
}
