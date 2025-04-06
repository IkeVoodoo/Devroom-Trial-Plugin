package me.ikevoodoo.devroomtrial.api.flags.events;

import org.bukkit.event.Event;

public abstract class FlagEvent extends Event {

    private final String flagId;

    protected FlagEvent(String flagId) {
        this.flagId = flagId;
    }

    public String getFlagId() {
        return flagId;
    }
}
