package me.ikevoodoo.devroomtrial.api.flags.events;

import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagPostUpdateEvent extends FlagEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final Region region;
    private final FlagState oldState;
    private final FlagState newState;

    public FlagPostUpdateEvent(Region region, String flagId, FlagState oldState, FlagState newState) {
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

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
