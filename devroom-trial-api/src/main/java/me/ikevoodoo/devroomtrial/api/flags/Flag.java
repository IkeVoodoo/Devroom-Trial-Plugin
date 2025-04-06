package me.ikevoodoo.devroomtrial.api.flags;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Flag implements Listener {

    private final String id;
    private final Region region;
    private FlagState state = FlagState.NONE;

    protected Flag(String id, Region region) {
        this.id = Objects.requireNonNull(id);
        this.region = region;
    }

    @NotNull
    public final String id() {
        return this.id;
    }

    @NotNull
    public final Region region() {
        return this.region;
    }

    @NotNull
    public final FlagState state() {
        return this.state;
    }

    @ApiStatus.Internal
    public final void updateState(@NotNull final FlagState flagState) {
        this.state = flagState;
    }

}
