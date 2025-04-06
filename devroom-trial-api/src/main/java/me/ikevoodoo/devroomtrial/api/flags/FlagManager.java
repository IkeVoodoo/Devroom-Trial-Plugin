package me.ikevoodoo.devroomtrial.api.flags;

import me.ikevoodoo.devroomtrial.api.RegionService;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public interface FlagManager {

    @ApiStatus.Internal
    class Holder {
        static final RegionService SERVICE = Objects.requireNonNull(Bukkit.getServicesManager().load(RegionService.class));

        private Holder() {}
    }

    @NotNull
    static FlagManager instance() {
        return Holder.SERVICE.getFlagRegistry();
    }

    void registerFlag(@NotNull final String id, @NotNull final FlagSupplier flag);

    @Nullable
    Flag createFlag(@NotNull final String id, @NotNull final Region region);

    @NotNull
    Set<String> getIds();

}
