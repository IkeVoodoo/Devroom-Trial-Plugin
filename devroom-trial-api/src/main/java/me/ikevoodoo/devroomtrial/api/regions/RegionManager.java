package me.ikevoodoo.devroomtrial.api.regions;

import me.ikevoodoo.devroomtrial.api.RegionService;
import org.bukkit.Bukkit;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface RegionManager {

    @ApiStatus.Internal
    class Holder {
        static final RegionService SERVICE = Objects.requireNonNull(Bukkit.getServicesManager().load(RegionService.class));

        private Holder() {}
    }

    @NotNull
    static RegionManager instance() {
        return Holder.SERVICE.getRegionManager();
    }

    @Nullable
    Region createRegion(@NotNull UUID creator, @NotNull final String name, @NotNull final UUID worldId, @NotNull final BoundingBox boundingBox);

    @Nullable
    Region getRegion(@NotNull final String name);

    @NotNull
    Collection<Region> listRegions();

    @NotNull
    Stream<String> listRegionNames(Predicate<String> region);
}
