package me.ikevoodoo.devroomtrial.api.whitelist;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface Whitelist extends Iterable<UUID> {

    @NotNull
    List<String> createItemLore(final int maxEntries);

    boolean contains(@NotNull final UUID uuid);

    default boolean contains(@NotNull final Entity entity) {
        return this.contains(entity.getUniqueId());
    }

    boolean add(@NotNull final UUID uuid);

    default void add(@NotNull final Entity entity) {
        this.add(entity.getUniqueId());
    }

    boolean remove(@NotNull final UUID uuid);

    default void remove(@NotNull final Entity entity) {
        this.remove(entity.getUniqueId());
    }

    int size();

}
