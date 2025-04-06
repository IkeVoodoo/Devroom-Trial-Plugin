package me.ikevoodoo.devroomtrial.api.regions.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class RegionEvent extends Event implements Cancellable {

    private final Region region;
    private boolean cancelled;

    protected RegionEvent(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
