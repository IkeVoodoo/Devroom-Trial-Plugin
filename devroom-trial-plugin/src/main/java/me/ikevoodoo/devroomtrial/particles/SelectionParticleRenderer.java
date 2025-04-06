package me.ikevoodoo.devroomtrial.particles;

import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public class SelectionParticleRenderer implements Consumer<BukkitTask> {

    private final UUID playerId;

    private final ParticleSpawnInfo particleSpawnInfo;
    private final Consumer<UUID> endTask;

    public SelectionParticleRenderer(@NotNull final UUID playerId,
                                     @NotNull final ParticleSpawnInfo particleSpawnInfo,
                                     @NotNull final Consumer<UUID> endTask) {
        this.playerId = playerId;
        this.particleSpawnInfo = particleSpawnInfo;
        this.endTask = endTask;
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        final var player = Bukkit.getPlayer(this.playerId);
        if (player == null || !player.isOnline()) {
            bukkitTask.cancel();
            this.endTask.accept(this.playerId);
            return;
        }

        final var selection = SelectionManager.instance().getSelection(player, player.getWorld());
        if (selection == null) {
            bukkitTask.cancel();
            this.endTask.accept(this.playerId);
            return;
        }

        final var adjusted = this.adjustBoundingBox(selection.clone());

        this.drawColumns(player, adjusted);
        this.drawSquares(player, adjusted);
    }

    private void drawColumns(Player player, BoundingBox boundingBox) {
        final var minX = boundingBox.getMinX();
        final var maxX = boundingBox.getMaxX();
        final var minY = boundingBox.getMinY();
        final var maxY = boundingBox.getMaxY();
        final var minZ = boundingBox.getMinZ();
        final var maxZ = boundingBox.getMaxZ();

        for (double y = minY; y <= maxY; y += this.particleSpawnInfo.step()) {
            this.particleSpawnInfo.spawn(player, minX, y, minZ);
            this.particleSpawnInfo.spawn(player, minX, y, maxZ);
            this.particleSpawnInfo.spawn(player, maxX, y, minZ);
            this.particleSpawnInfo.spawn(player, maxX, y, maxZ);
        }
    }

    private void drawSquares(Player player, BoundingBox boundingBox) {
        final var minX = boundingBox.getMinX();
        final var maxX = boundingBox.getMaxX();
        final var minY = boundingBox.getMinY();
        final var maxY = boundingBox.getMaxY();
        final var minZ = boundingBox.getMinZ();
        final var maxZ = boundingBox.getMaxZ();

        for (double x = minX; x <= maxX; x += this.particleSpawnInfo.step()) {
            this.particleSpawnInfo.spawn(player, x, minY, minZ);
            this.particleSpawnInfo.spawn(player, x, minY, maxZ);
            this.particleSpawnInfo.spawn(player, x, maxY, minZ);
            this.particleSpawnInfo.spawn(player, x, maxY, maxZ);
        }

        for (double z = minZ; z <= maxZ; z += this.particleSpawnInfo.step()) {
            this.particleSpawnInfo.spawn(player, minX, minY, z);
            this.particleSpawnInfo.spawn(player, maxX, minY, z);
            this.particleSpawnInfo.spawn(player, minX, maxY, z);
            this.particleSpawnInfo.spawn(player, maxX, maxY, z);
        }
    }

    private BoundingBox adjustBoundingBox(BoundingBox boundingBox) {
        boundingBox.resize(
                boundingBox.getMaxX() + 1,
                boundingBox.getMaxY() + 1,
                boundingBox.getMaxZ() + 1,
                boundingBox.getMinX(),
                boundingBox.getMinY(),
                boundingBox.getMinZ()
        );

        return boundingBox;
    }

}
