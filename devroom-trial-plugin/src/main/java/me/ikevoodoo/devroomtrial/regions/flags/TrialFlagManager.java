package me.ikevoodoo.devroomtrial.regions.flags;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagManager;
import me.ikevoodoo.devroomtrial.api.flags.FlagSupplier;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagRegisterEvent;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrialFlagManager implements FlagManager {

    private final Map<String, FlagSupplier> flags = new HashMap<>();
    private final Set<String> flagIdView = Collections.unmodifiableSet(this.flags.keySet());

    @Override
    public void registerFlag(@NotNull final String id, @NotNull final FlagSupplier flag) {
        Objects.requireNonNull(id, "Cannot register flag with null id!");
        Objects.requireNonNull(flag, "Cannot register null flag!");

        final var existing = this.flags.putIfAbsent(id, flag);
        if (existing != null) {
            return;
        }

        Bukkit.getPluginManager().callEvent(new FlagRegisterEvent(id));
    }

    @Override
    public @Nullable Flag createFlag(@NotNull String id, @NotNull Region region) {
        final var supplier = this.flags.get(id);
        if (supplier == null) {
            return null;
        }

        final var flag = supplier.createFlag(id, region);

        final var plugin = JavaPlugin.getProvidingPlugin(supplier.getClass());

        Bukkit.getPluginManager().registerEvents(flag, plugin);

        return flag;
    }

    @Override
    public @NotNull Set<String> getIds() {
        return this.flagIdView;
    }
}
