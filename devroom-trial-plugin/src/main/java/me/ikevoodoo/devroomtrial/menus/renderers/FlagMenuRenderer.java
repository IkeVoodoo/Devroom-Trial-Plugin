package me.ikevoodoo.devroomtrial.menus.renderers;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.menus.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlagMenuRenderer implements MenuRenderer {

    private static final int FLAGS_PER_PAGE = 9 * 6;

    private final Region region;
    private final NamespacedKey idKey;
    private final NamespacedKey stateKey;

    public FlagMenuRenderer(Region region) {
        this.region = region;

        final var plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        this.idKey = new NamespacedKey(plugin, "flag_id");
        this.stateKey = new NamespacedKey(plugin, "state_id");
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull Menu menu, @NotNull Player player, int page) {
        final var flags = this.region.flags().size();
        final var currentPages = (int) Math.ceil(flags / (double) (FLAGS_PER_PAGE - 1));

        return Bukkit.createInventory(null, FLAGS_PER_PAGE, "Edit Flags " + (page + 1) + " / " + currentPages);
    }

    @Override
    public void render(@NotNull Menu menu, @NotNull Player player, @NotNull Inventory inventory, int page) {
        int skip = page * inventory.getSize();
        int slot = 0;
        for (final var flag : this.region.flags()) {
            if (skip > 0) {
                skip--;
                continue;
            }

            final var stack = this.createFlagStack(flag);

            inventory.setItem(slot, stack);

            slot++;
            if (slot == 49) {
                slot++;
            }

            if (slot >= inventory.getSize()) {
                break;
            }
        }

        final var stack = new ItemStack(Material.ENDER_EYE);
        final var meta = stack.getItemMeta();
        assert meta != null;

        meta.setDisplayName("§aPage Switcher");

        meta.setLore(List.of(
                "§6Left-Click §7to go to the previous page",
                "§bRight-Click §7to go to the next page"
        ));

        stack.setItemMeta(meta);

        final var paginatorPrev = menu.makePaginator(stack, "edit_flags_" + this.region.uniqueId() + (page - 1), true);
        final var paginatorNext = menu.makePaginator(paginatorPrev, "edit_flags_" + this.region.uniqueId() + (page + 1), false);

        inventory.setItem(49, paginatorNext);
    }

    @Override
    public void onClick(@NotNull Menu menu, @NotNull Player player, @NotNull Inventory inventory, int page, int slot) {
        final var stack = inventory.getItem(slot);
        if (stack == null) {
            return;
        }

        final var meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }

        final var pdc = meta.getPersistentDataContainer();
        if (!pdc.has(this.idKey)) {
            return;
        }

        final var id = Objects.requireNonNull(pdc.get(this.idKey, PersistentDataType.STRING));
        final var ordinal = Objects.requireNonNull(pdc.get(this.stateKey, PersistentDataType.INTEGER));

        this.region.flags().updateState(id, FlagState.values()[(ordinal + 1) % 3]);

        this.render(menu, player, inventory, page);
    }

    private ItemStack createFlagStack(Flag flag) {
        final var stack = new ItemStack(Material.OAK_SIGN);
        stack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);

        final var meta = stack.getItemMeta();
        assert meta != null;

        // Give a glowing effect without showing enchantments
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setDisplayName(flag.id());

        final var lore = new ArrayList<String>();

        for (final var value : FlagState.values()) {
            if (value == flag.state()) {
                lore.add("§7 - §b%s".formatted(value.name()));
                continue;
            }

            lore.add("§7 - %s".formatted(value.name()));
        }

        final var pdc = meta.getPersistentDataContainer();
        pdc.set(this.idKey, PersistentDataType.STRING, flag.id());
        pdc.set(this.stateKey, PersistentDataType.INTEGER, flag.state().ordinal());

        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }
}
