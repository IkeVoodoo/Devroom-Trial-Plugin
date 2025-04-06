package me.ikevoodoo.devroomtrial.api.regions.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegionResizeEvent extends RegionEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final BoundingBox previous;
    private BoundingBox current;

    public RegionResizeEvent(Region region, BoundingBox previous, BoundingBox current) {
        super(region);
        this.previous = previous;
        this.current = current;
    }

    @NotNull
    public BoundingBox getPrevious() {
        return this.previous.clone();
    }

    @NotNull
    public BoundingBox getCurrent() {
        return this.current.clone();
    }

    public void setCurrent(@NotNull BoundingBox boundingBox) {
        Objects.requireNonNull(boundingBox);

        this.current = boundingBox;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
