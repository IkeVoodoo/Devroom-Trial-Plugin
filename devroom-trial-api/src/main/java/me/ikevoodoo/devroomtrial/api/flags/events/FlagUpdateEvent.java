package me.ikevoodoo.devroomtrial.api.flags.events;

import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FlagUpdateEvent extends FlagEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final Region region;
    private final FlagState oldState;
    private FlagState newState;

    public FlagUpdateEvent(Region region, String flagId, FlagState oldState, FlagState newState) {
        super(flagId);
        this.region = region;
        this.oldState = oldState;
        this.newState = newState;
    }

    public Region getRegion() {
        return region;
    }

    public FlagState getOldState() {
        return oldState;
    }

    public FlagState getNewState() {
        return newState;
    }

    public void setNewState(@NotNull FlagState flagState) {
        Objects.requireNonNull(flagState);

        this.newState = flagState;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
