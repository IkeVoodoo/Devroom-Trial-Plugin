package me.ikevoodoo.devroomtrial.api.whitelist.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionEvent;
import me.ikevoodoo.devroomtrial.api.whitelist.Whitelist;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WhitelistRemoveEvent extends RegionEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final Whitelist whitelist;
    private final UUID entityId;

    public WhitelistRemoveEvent(Region region, Whitelist whitelist, UUID entityId) {
        super(region);
        this.whitelist = whitelist;
        this.entityId = entityId;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
