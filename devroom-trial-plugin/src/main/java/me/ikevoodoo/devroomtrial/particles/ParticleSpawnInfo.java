package me.ikevoodoo.devroomtrial.particles;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParticleSpawnInfo(@NotNull Particle particle, @Nullable Object data, int count, double step) {

    public void spawn(@NotNull final Player player, final double x, final double y, final double z) {
        player.spawnParticle(this.particle, x, y, z, this.count, this.data);
    }

}
