package me.ikevoodoo.devroomtrial.regions.whitelist;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.whitelist.Whitelist;
import me.ikevoodoo.devroomtrial.api.whitelist.events.WhitelistAddEvent;
import me.ikevoodoo.devroomtrial.api.whitelist.events.WhitelistRemoveEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TrialWhitelist implements Whitelist {

    private final Set<UUID> whitelist = new HashSet<>();
    private final Set<UUID> whitelistView = Collections.unmodifiableSet(this.whitelist);

    private Region parent;

    @Override
    public @NotNull List<String> createItemLore(int maxEntries) {
        final var lore = new ArrayList<String>();
        if (this.size() == 0) {
            lore.add("§7No allowed players.");
            return lore;
        }

        lore.add("§7Allowed Players:");

        for (final var id : this) {
            if (maxEntries <= 0) {
                lore.add(" §7... and %d more.".formatted(this.size() - 5));
                break;
            }

            final var player = Bukkit.getOfflinePlayer(id);

            final var name = player.getPlayer() != null
                    ? player.getPlayer().getDisplayName()
                    : player.getName();

            lore.add(" §7- §f%s".formatted(name));

            maxEntries--;
        }

        return lore;
    }

    @Override
    public boolean contains(@NotNull UUID uuid) {
        return this.whitelist.contains(uuid);
    }

    @Override
    public boolean add(@NotNull UUID uuid) {
        final var event = new WhitelistAddEvent(this.parent, this, uuid);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.whitelist.add(uuid);
        return true;
    }

    @Override
    public boolean remove(@NotNull UUID uuid) {
        final var event = new WhitelistRemoveEvent(this.parent, this, uuid);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.whitelist.remove(uuid);
        return true;
    }

    @Override
    public int size() {
        return this.whitelist.size();
    }

    @Override
    public @NotNull Iterator<UUID> iterator() {
        return this.whitelistView.iterator();
    }

    public void setParent(Region region) {
        this.parent = region;
    }
}
