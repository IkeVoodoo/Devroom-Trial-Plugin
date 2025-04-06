package me.ikevoodoo.devroomtrial.api.regions;

import me.ikevoodoo.devroomtrial.api.RegionService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface SelectionManager {

    @ApiStatus.Internal
    class Holder {
        static final RegionService SERVICE = Objects.requireNonNull(Bukkit.getServicesManager().load(RegionService.class));

        private Holder() {}
    }

    @NotNull
    static SelectionManager instance() {
        return Holder.SERVICE.getSelectionManager();
    }

    boolean isWand(@NotNull final ItemStack itemStack);

    @NotNull
    ItemStack createWand();

    void updateSelectionStart(@NotNull final Entity entity, @NotNull final Vector pos);

    void updateSelectionEnd(@NotNull final Entity entity, @NotNull final Vector pos);

    void resetSelection(@NotNull final Entity entity, @NotNull final World world);

    @Nullable
    BoundingBox getSelection(@NotNull final Entity entity, @NotNull final World world);

}
