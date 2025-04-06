package me.ikevoodoo.devroomtrial.api.regions;

import me.ikevoodoo.devroomtrial.api.RegionService;
import me.ikevoodoo.devroomtrial.api.flags.FlagSet;
import me.ikevoodoo.devroomtrial.api.whitelist.Whitelist;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public interface Region {

    @ApiStatus.Internal
    class Holder {
        static final RegionService SERVICE = Objects.requireNonNull(Bukkit.getServicesManager().load(RegionService.class));

        private Holder() {}
    }

    @Nullable
    static Region create(@NotNull final UUID creator, @NotNull final String name, @NotNull final World world, @NotNull final BoundingBox boundingBox) {
        return Holder.SERVICE.getRegionManager().createRegion(creator, name, world.getUID(), boundingBox);
    }

    @NotNull
    UUID uniqueId();

    @NotNull
    String name();

    @NotNull
    UUID worldId();

    @NotNull
    BoundingBox boundingBox();

    @NotNull
    FlagSet flags();

    @NotNull
    Whitelist whitelist();

    boolean updateName(@NotNull final String name);

    boolean updatePosition(@NotNull final BoundingBox boundingBox);
}
