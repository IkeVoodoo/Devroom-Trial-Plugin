package me.ikevoodoo.devroomtrial.api.flags;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FlagSupplier {

    @NotNull
    Flag createFlag(@NotNull final String id, @NotNull final Region region);

}
