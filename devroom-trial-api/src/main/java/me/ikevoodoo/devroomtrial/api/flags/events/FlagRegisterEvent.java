package me.ikevoodoo.devroomtrial.api.flags.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagRegisterEvent extends FlagEvent {

    private static final HandlerList handlerList = new HandlerList();

    public FlagRegisterEvent(String flagId) {
        super(flagId);
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
