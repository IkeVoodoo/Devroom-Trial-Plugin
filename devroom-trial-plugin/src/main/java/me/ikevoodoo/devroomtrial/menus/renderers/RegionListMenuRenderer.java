package me.ikevoodoo.devroomtrial.menus.renderers;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import me.ikevoodoo.devroomtrial.api.whitelist.Whitelist;
import me.ikevoodoo.devroomtrial.menus.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RegionListMenuRenderer implements MenuRenderer {

    private static final int REGIONS_PER_PAGE = 9 * 6;

    @Override
    public @NotNull Inventory createInventory(@NotNull Menu menu, @NotNull Player player, int page) {
        final var regions = RegionManager.instance().listRegions();
        final var regionCount = regions.size();
        final var currentPages = (int) Math.ceil(regionCount / (double) (REGIONS_PER_PAGE - 1));

        return  Bukkit.createInventory(null, REGIONS_PER_PAGE, "Regions " + (page + 1) + " / " + currentPages);
    }

    @Override
    public void render(@NotNull Menu menu, @NotNull Player player, @NotNull Inventory inventory, int page) {
        int skip = page * inventory.getSize();
        int slot = 0;
        for (final var region : RegionManager.instance().listRegions()) {
            if (skip > 0) {
                skip--;
                continue;
            }

            final var stack = this.createRegionStack(region);
            final var paginator = menu.makePaginator(stack, "edit_region_" + region.uniqueId());

            inventory.setItem(slot, paginator);

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

        final var paginatorPrev = menu.makePaginator(stack, "list_regions_" + (page - 1), true);
        final var paginatorNext = menu.makePaginator(paginatorPrev, "list_regions_" + (page + 1), false);

        inventory.setItem(49, paginatorNext);
    }

    private ItemStack createRegionStack(Region region) {
        final var stack = new ItemStack(Material.OAK_SIGN);
        stack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);

        final var meta = stack.getItemMeta();
        assert meta != null;

        // Give a glowing effect without showing enchantments
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setDisplayName(region.name());

        final var bb = region.boundingBox();

        final var lore = new ArrayList<>(List.of(
                "§7Bounding Box:",
                "§7  Min x: §c%d§7, y: §a%d§7, z: §3%d".formatted((int) bb.getMinX(), (int) bb.getMinY(), (int) bb.getMinZ()),
                "§7  Max x: §c%d§7, y: §a%d§7, z: §3%d".formatted((int) bb.getMaxX(), (int) bb.getMaxY(), (int) bb.getMaxZ()),
                "§r"
        ));

        lore.addAll(region.whitelist().createItemLore(5));

        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }
}
