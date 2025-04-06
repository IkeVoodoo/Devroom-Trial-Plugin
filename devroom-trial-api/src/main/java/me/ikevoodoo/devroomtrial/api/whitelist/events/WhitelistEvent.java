package me.ikevoodoo.devroomtrial.api.whitelist.events;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionEvent;
import me.ikevoodoo.devroomtrial.api.whitelist.Whitelist;

public abstract class WhitelistEvent extends RegionEvent {

    private final Whitelist whitelist;

    public WhitelistEvent(Region region, Whitelist whitelist) {
        super(region);
        this.whitelist = whitelist;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }
}
