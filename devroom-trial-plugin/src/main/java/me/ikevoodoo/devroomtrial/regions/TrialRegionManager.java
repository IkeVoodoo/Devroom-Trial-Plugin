package me.ikevoodoo.devroomtrial.regions;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionCreateEvent;
import me.ikevoodoo.devroomtrial.regions.flags.TrialFlagSet;
import me.ikevoodoo.devroomtrial.regions.whitelist.TrialWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TrialRegionManager implements RegionManager {

    private final Map<String, Region> regions = new LinkedHashMap<>();
    private final Collection<Region> regionView = Collections.unmodifiableCollection(this.regions.values());

    public boolean addRegion(Region region) {
        final var event = new RegionCreateEvent(region);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.regions.put(region.name(), region);
        return true;
    }

    @Nullable
    @Override
    public Region createRegion(@NotNull UUID creator, @NotNull final String name, @NotNull final UUID worldId, @NotNull final BoundingBox boundingBox) {
        if (this.regions.containsKey(name)) {
            return null;
        }

        final var region = new TrialRegion(
                UUID.randomUUID(),
                name,
                worldId,
                boundingBox,
                new TrialFlagSet(),
                new TrialWhitelist()
        );

        region.whitelist().add(creator);

        if(!this.addRegion(region)) {
            return null;
        }

        return region;
    }

    @Nullable
    @Override
    public Region getRegion(@NotNull final String name) {
        return this.regions.get(name);
    }

    @NotNull
    @Override
    public Collection<Region> listRegions() {
        return this.regionView;
    }

    @NotNull
    @Override
    public Stream<String> listRegionNames(Predicate<String> region) {
        return this.regionView.stream().map(Region::name).filter(region);
    }
}
