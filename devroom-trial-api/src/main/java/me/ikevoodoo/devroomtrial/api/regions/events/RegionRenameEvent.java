package me.ikevoodoo.devroomtrial.api.regions.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegionRenameEvent extends RegionEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final String previous;
    private String current;

    public RegionRenameEvent(Region region, String previous, String current) {
        super(region);
        this.previous = previous;
        this.current = current;
    }

    @NotNull
    public String getPrevious() {
        return this.previous;
    }

    @NotNull
    public String getCurrent() {
        return this.current;
    }

    public void setCurrent(@NotNull String name) {
        Objects.requireNonNull(name);

        this.current = name;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
