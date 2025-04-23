package me.ikevoodoo.devroomtrial.menus;

import me.ikevoodoo.devroomtrial.menus.renderers.MenuRenderer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Menu {

    private final NamespacedKey paginatorKeyLeft;
    private final NamespacedKey paginatorKeyRight;

    private final Map<UUID, Deque<MenuPage>> viewers = new HashMap<>();
    private final Set<UUID> transitioning = new HashSet<>();
    private final Map<String, Integer> pageGroupCounters = new HashMap<>();
    private final Map<String, MenuPage> pagesByName = new LinkedHashMap<>();

    public Menu(NamespacedKey paginatorKeyLeft, NamespacedKey paginatorKeyRight) {
        this.paginatorKeyLeft = paginatorKeyLeft;
        this.paginatorKeyRight = paginatorKeyRight;
    }

    @Nullable
    public MenuPage addPage(@NotNull final String group, @NotNull final String pageName, @NotNull final MenuRenderer renderer) {
        if (this.pagesByName.containsKey(pageName)) {
            return null;
        }

        final var id = this.pageGroupCounters.compute(group, (s, integer) -> integer == null ? 0 : integer + 1);
        final var page = new MenuPage(pageName, id, renderer);
        this.pagesByName.put(pageName, page);

        return page;
    }

    public MenuPage getPage(@NotNull final Player player) {
        return this.viewers.getOrDefault(player.getUniqueId(), new ArrayDeque<>()).peekLast();
    }

    public void closeAll(@NotNull final Player player) {
        final var uuid = player.getUniqueId();
        this.viewers.remove(uuid);
        this.transitioning.remove(uuid);
        player.closeInventory();
    }

    public void startTransitioning(@NotNull final Player player) {
        this.transitioning.add(player.getUniqueId());
    }

    public void stopTransitioning(@NotNull final Player player) {
        this.transitioning.remove(player.getUniqueId());
    }

    public boolean isTransitioning(@NotNull final Player player) {
        return this.transitioning.contains(player.getUniqueId());
    }

    public Deque<MenuPage> saveHistory(@NotNull final Player player) {
        return new ArrayDeque<>(this.viewers.getOrDefault(player.getUniqueId(), new ArrayDeque<>()));
    }

    public void restoreHistory(@NotNull final Player player, @NotNull final Deque<MenuPage> queue) {
        final var existing = this.viewers.computeIfAbsent(player.getUniqueId(), id -> new ArrayDeque<>());

        queue.descendingIterator().forEachRemaining(existing::addFirst);
    }

    public void close(@NotNull final Player player) {
        final var uuid = player.getUniqueId();

        final var open = this.viewers.get(uuid);
        if (open == null) {
            return;
        }

        open.pollLast();

        final var previous = open.peekLast();
        if (previous != null) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this.getClass()), () -> {
                this.startTransitioning(player);
                previous.open(this, player);
                this.stopTransitioning(player);
            }, 1);
            return;
        }

        this.viewers.remove(uuid);
        player.closeInventory();
    }

    public boolean open(@NotNull final Player player, @NotNull final String pageName) {
        final var page = this.pagesByName.get(pageName);
        if (page == null) {
            return false;
        }

        return this.openPage(player, page);
    }

    public boolean openFromPaginator(@NotNull final Player player, @NotNull final ItemStack paginatorStack, final boolean leftClick) {
        final var meta = paginatorStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        final var key = this.getPaginatorKey(leftClick);

        final var pdc = meta.getPersistentDataContainer();
        final var paginatorName = pdc.get(key, PersistentDataType.STRING);

        if (paginatorName != null) {
            return this.open(player, paginatorName);
        }

        return false;
    }

    public boolean isPaginator(@NotNull final ItemStack stack) {
        final var meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }

        final var pdc = meta.getPersistentDataContainer();
        return pdc.has(this.paginatorKeyLeft) || pdc.has(this.paginatorKeyRight);
    }

    @NotNull
    public ItemStack makePaginator(@NotNull final ItemStack itemStack, @NotNull final String name, final boolean leftClick) {
        return this.makePaginator(this.getPaginatorKey(leftClick), itemStack, PersistentDataType.STRING, name);
    }

    @NotNull
    public ItemStack makePaginator(@NotNull final ItemStack itemStack, @NotNull final String name) {
        final var stack = this.makePaginator(this.paginatorKeyLeft, itemStack, PersistentDataType.STRING, name);

        return this.makePaginator(this.paginatorKeyRight, stack, PersistentDataType.STRING, name);
    }

    private NamespacedKey getPaginatorKey(boolean leftClick) {
        return leftClick ? this.paginatorKeyLeft : this.paginatorKeyRight;
    }

    private <P, C> ItemStack makePaginator(NamespacedKey key, ItemStack itemStack, PersistentDataType<P, C> type, C value) {
        final var meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }

        final var pdc = meta.getPersistentDataContainer();
        pdc.set(key, type, value);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private boolean openPage(Player player, MenuPage page) {
        final var uuid = player.getUniqueId();

        final var existing = this.viewers.computeIfAbsent(uuid, unused -> new ArrayDeque<>());

        existing.addLast(page);

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this.getClass()), () -> {
            this.startTransitioning(player);
            page.open(this, player);
            this.stopTransitioning(player);
        }, 1);
        return true;
    }

}
