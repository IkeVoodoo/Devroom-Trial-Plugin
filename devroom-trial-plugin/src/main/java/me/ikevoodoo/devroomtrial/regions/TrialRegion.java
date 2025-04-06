package me.ikevoodoo.devroomtrial.regions;

import me.ikevoodoo.devroomtrial.api.flags.FlagSet;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionRenameEvent;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionResizeEvent;
import me.ikevoodoo.devroomtrial.api.whitelist.Whitelist;
import me.ikevoodoo.devroomtrial.regions.flags.TrialFlagSet;
import me.ikevoodoo.devroomtrial.regions.whitelist.TrialWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class TrialRegion implements Region {
    private final @NotNull UUID uniqueId;
    private @NotNull String name;
    private final @NotNull UUID worldId;
    private final @NotNull BoundingBox boundingBox;
    private final @NotNull FlagSet flags;
    private final @NotNull Whitelist whitelist;

    public TrialRegion(
            @NotNull UUID uniqueId,
            @NotNull String name, @NotNull UUID worldId,
            @NotNull BoundingBox boundingBox,
            @NotNull TrialFlagSet flags,
            @NotNull TrialWhitelist whitelist
    ) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.worldId = worldId;
        this.boundingBox = boundingBox;
        this.flags = flags;
        this.whitelist = whitelist;

        flags.setParent(this);
        whitelist.setParent(this);
    }

    @Override
    public boolean updateName(@NotNull String name) {
        final var event = new RegionRenameEvent(this, this.name, name);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.name = event.getCurrent();
        return true;
    }

    @Override
    public boolean updatePosition(@NotNull BoundingBox boundingBox) {
        final var event = new RegionResizeEvent(this, this.boundingBox, boundingBox);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.boundingBox.copy(boundingBox);
        return true;
    }

    @Override
    public @NotNull UUID uniqueId() {
        return uniqueId;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull UUID worldId() {
        return this.worldId;
    }

    @Override
    public @NotNull BoundingBox boundingBox() {
        return boundingBox;
    }

    @Override
    public @NotNull FlagSet flags() {
        return flags;
    }

    @Override
    public @NotNull Whitelist whitelist() {
        return whitelist;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (TrialRegion) obj;
        return Objects.equals(this.uniqueId, that.uniqueId) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.boundingBox, that.boundingBox) &&
                Objects.equals(this.flags, that.flags) &&
                Objects.equals(this.whitelist, that.whitelist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, name, boundingBox, flags, whitelist);
    }

    @Override
    public String toString() {
        return "TrialRegion[" +
                "uniqueId=" + uniqueId + ", " +
                "name=" + name + ", " +
                "boundingBox=" + boundingBox + ", " +
                "flags=" + flags + ", " +
                "whitelist=" + whitelist + ']';
    }

}
