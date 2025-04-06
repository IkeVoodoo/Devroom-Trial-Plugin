package me.ikevoodoo.devroomtrial.api.flags.events;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagAddEvent extends FlagEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final Region region;
    private final Flag flag;

    public FlagAddEvent(Region region, Flag flag) {
        super(flag.id());
        this.region = region;
        this.flag = flag;
    }

    public Region getRegion() {
        return region;
    }

    public Flag getFlag() {
        return flag;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
