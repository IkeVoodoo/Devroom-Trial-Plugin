package me.ikevoodoo.devroomtrial.regions;

import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrialSelectionManager implements SelectionManager {

    private final NamespacedKey wandKey;

    public TrialSelectionManager(NamespacedKey wandKey) {
        this.wandKey = wandKey;
    }

    @Override
    public boolean isWand(@NotNull final ItemStack stack) {
        final var meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }

        final var pdc = meta.getPersistentDataContainer();

        // We use Boolean.TRUE because get() returns a Boolean object and it may be null
        return Boolean.TRUE == pdc.get(this.wandKey, PersistentDataType.BOOLEAN);
    }

    @NotNull
    @Override
    public ItemStack createWand() {
        final var stack = new ItemStack(Material.STONE_AXE);
        final var meta = stack.getItemMeta();
        assert meta != null;

        meta.setDisplayName("§aRegion Selection Wand");

        final var pdc = meta.getPersistentDataContainer();
        pdc.set(this.wandKey, PersistentDataType.BOOLEAN, true);

        meta.setLore(List.of(
                "§6Left-Click §7to set the first position",
                "§bRight-Click §7to set the second position"
        ));

        stack.setItemMeta(meta);

        return stack;
    }

    @Override
    public void updateSelectionStart(@NotNull final Entity entity, @NotNull final Vector pos) {
        this.updateEntityPos(this.getStartKey(entity.getWorld()), entity, pos);
    }

    @Override
    public void updateSelectionEnd(@NotNull final Entity entity, @NotNull final Vector pos) {
        this.updateEntityPos(this.getEndKey(entity.getWorld()), entity, pos);
    }

    @Override
    public void resetSelection(@NotNull final Entity entity, @NotNull final World world) {
        final var pdc = entity.getPersistentDataContainer();
        pdc.remove(this.getStartKey(world));
        pdc.remove(this.getEndKey(world));
    }

    @Nullable
    public BoundingBox getSelection(@NotNull Entity entity, @NotNull World world) {
        final var pdc = entity.getPersistentDataContainer();
        final var min = this.readVector(this.getStartKey(world), pdc);
        if (min == null) {
            return null;
        }

        final var max = this.readVector(this.getEndKey(world), pdc);
        if (max == null) {
            return null;
        }

        return new BoundingBox(
                min.getX(),
                min.getY(),
                min.getZ(),

                max.getX(),
                max.getY(),
                max.getZ()
        );
    }

    private void updateEntityPos(NamespacedKey key, Entity entity, Vector pos) {
        final var pdc = entity.getPersistentDataContainer();
        final var arr = new int[] {
                pos.getBlockX(),
                pos.getBlockY(),
                pos.getBlockZ()
        };

        pdc.set(key, PersistentDataType.INTEGER_ARRAY, arr);
    }

    private Vector readVector(NamespacedKey key, PersistentDataContainer pdc) {
        final var arr = pdc.getOrDefault(key, PersistentDataType.INTEGER_ARRAY, new int[0]);
        if (arr.length == 0) {
            return null;
        }

        return new Vector(
                arr[0],
                arr[1],
                arr[2]
        );
    }

    private NamespacedKey getStartKey(World world) {
        return new NamespacedKey(
                JavaPlugin.getProvidingPlugin(this.getClass()),
                "selection_start_" + world.getUID()
        );
    }

    private NamespacedKey getEndKey(World world) {
        return new NamespacedKey(
                JavaPlugin.getProvidingPlugin(this.getClass()),
                "selection_end_" + world.getUID()
        );
    }

}
