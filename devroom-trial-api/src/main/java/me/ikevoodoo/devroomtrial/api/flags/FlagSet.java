package me.ikevoodoo.devroomtrial.api.flags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface FlagSet extends Iterable<Flag> {

    void addFlag(@NotNull final Flag flag);

    @Nullable
    Flag removeFlag(@NotNull final String id);

    @Nullable
    Flag getFlag(@NotNull final String id);

    void updateState(@NotNull final String id, @NotNull final FlagState state);

    @NotNull
    Set<String> getFlagIds();

    int size();

}
