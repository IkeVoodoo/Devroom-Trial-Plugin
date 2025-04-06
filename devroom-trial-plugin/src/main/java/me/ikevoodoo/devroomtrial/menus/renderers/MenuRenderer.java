package me.ikevoodoo.devroomtrial.menus.renderers;

import me.ikevoodoo.devroomtrial.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface MenuRenderer {

    @NotNull
    Inventory createInventory(@NotNull final Menu menu, @NotNull final Player player, int page);

    void render(@NotNull final Menu menu, @NotNull final Player player, @NotNull final Inventory inventory, int page);

    default void onClick(@NotNull final Menu menu, @NotNull final Player player, @NotNull final Inventory inventory, int page, int slot) {

    }

}
