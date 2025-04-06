package me.ikevoodoo.devroomtrial.listeners;

import me.ikevoodoo.devroomtrial.api.flags.FlagManager;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagAddEvent;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagRegisterEvent;
import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionCreateEvent;
import me.ikevoodoo.devroomtrial.menus.Menu;
import me.ikevoodoo.devroomtrial.menus.renderers.FlagMenuRenderer;
import me.ikevoodoo.devroomtrial.menus.renderers.RegionListMenuRenderer;
import me.ikevoodoo.devroomtrial.menus.renderers.RegionMenuRenderer;
import me.ikevoodoo.devroomtrial.regions.TrialRegion;
import me.ikevoodoo.devroomtrial.regions.flags.TrialFlagSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class RegionListener implements Listener {

    private final Menu menu;

    public RegionListener(Menu menu) {
        this.menu = menu;
    }

    @EventHandler
    public void onRegionCreate(RegionCreateEvent event) {
        final var regions = RegionManager.instance().listRegions();

        final var count = regions.size();

        final var perPage = (9 * 6) - 1D;
        final var currentPageCount = count == 0 ? 0 : (int) Math.ceil(count / perPage);
        final var nextPageCount = (int) Math.ceil((count + 1) / perPage);

        if (nextPageCount > currentPageCount) {
            this.menu.addPage("list_regions", "list_regions_" + currentPageCount, new RegionListMenuRenderer());
        }

        final var region = event.getRegion();
        this.menu.addPage("edit_region_" + region.uniqueId(), "edit_region_" + region.uniqueId(), new RegionMenuRenderer(region));

        if (region.flags().size() > 0) {
            return;
        }

        final var flagManager = FlagManager.instance();

        // Init flags
        for (final var id : flagManager.getIds()) {
            region.flags().addFlag(Objects.requireNonNull(flagManager.createFlag(id, region)));
        }
    }

    @EventHandler
    public void onFlagAdd(FlagAddEvent event) {
        final var region = event.getRegion();
        final var count = region.flags().size();

        final var perPage = (9 * 6) - 1D;
        final var currentPageCount = count == 0 ? 0 : (int) Math.ceil(count / perPage);
        final var nextPageCount = (int) Math.ceil((count + 1) / perPage);

        if (nextPageCount > currentPageCount) {
            this.menu.addPage("edit_flags_" + region.uniqueId(), "edit_flags_" + region.uniqueId() + currentPageCount, new FlagMenuRenderer(region));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlagRegister(FlagRegisterEvent event) {
        final var id = event.getFlagId();
        final var flagManager = FlagManager.instance();

        for (final var region : RegionManager.instance().listRegions()) {
            ((TrialFlagSet) region.flags()).addFlag(Objects.requireNonNull(flagManager.createFlag(id, region)));
        }
    }


}
