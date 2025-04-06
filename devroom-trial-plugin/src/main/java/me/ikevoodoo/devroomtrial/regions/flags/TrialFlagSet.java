package me.ikevoodoo.devroomtrial.regions.flags;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagSet;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagAddEvent;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagPostUpdateEvent;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagUpdateEvent;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.*;

public class TrialFlagSet implements FlagSet {

    private final Map<String, Flag> flags = new HashMap<>();
    private final Set<String> flagIds = Collections.unmodifiableSet(this.flags.keySet());
    private final Collection<Flag> flagValues = Collections.unmodifiableCollection(this.flags.values());
    private Region parent;

    @Override
    public void addFlag(@NotNull final Flag flag) {
        Objects.requireNonNull(flag, "Cannot add null flag!");

        Bukkit.getPluginManager().callEvent(new FlagAddEvent(this.parent, flag));

        this.flags.put(flag.id(), flag);
    }

    @Nullable
    @Override
    public Flag removeFlag(@NotNull final String id) {
        Objects.requireNonNull(id, "Cannot remove flag with null id!");

        return this.flags.remove(id);
    }

    @Nullable
    @Override
    public Flag getFlag(@NotNull final String id) {
        return this.flags.get(id);
    }

    @Override
    public void updateState(@NotNull String id, @NotNull FlagState state) {
        final var flag = this.getFlag(id);
        if (flag == null) {
            return;
        }

        final var old = flag.state();
        final var updateEvent = new FlagUpdateEvent(this.parent, id, old, state);
        Bukkit.getPluginManager().callEvent(updateEvent);

        final var newState = updateEvent.getNewState();

        flag.updateState(newState);

        final var postUpdateEvent = new FlagPostUpdateEvent(this.parent, id, old, newState);
        Bukkit.getPluginManager().callEvent(postUpdateEvent);
    }

    @Override
    public @NotNull Set<String> getFlagIds() {
        return this.flagIds;
    }

    @Override
    public int size() {
        return this.flags.size();
    }

    public void setParent(Region parent) {
        this.parent = parent;
    }

    public void serialize(@NotNull final ObjectOutput output) throws IOException {
        output.writeInt(this.flags.size());

        for (final var entry : this.flags.entrySet()) {
            final var id = entry.getKey();
            final var flag = entry.getValue();

            output.writeUTF(id);
            output.writeInt(flag.state().ordinal());
        }
    }

    @Override
    public @NotNull Iterator<Flag> iterator() {
        return this.flagValues.iterator();
    }
}
