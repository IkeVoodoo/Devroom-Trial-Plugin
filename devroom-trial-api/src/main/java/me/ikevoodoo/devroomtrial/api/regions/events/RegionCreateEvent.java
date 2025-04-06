package me.ikevoodoo.devroomtrial.api.regions.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionCreateEvent extends RegionEvent {

    private static final HandlerList handlerList = new HandlerList();

    public RegionCreateEvent(Region region) {
        super(region);
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
